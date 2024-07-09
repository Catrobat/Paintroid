package org.catrobat.paintroid

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.catrobat.paintroid.common.ABOUT_DIALOG_FRAGMENT_TAG
import org.catrobat.paintroid.common.REQUEST_CODE_INTRO
import org.catrobat.paintroid.common.RESULT_INTRO_MW_NOT_SUPPORTED
import org.catrobat.paintroid.dialog.AboutDialog
import org.catrobat.paintroid.ui.ToastFactory
import org.catrobat.paintroid.web.PlayStore

class LandingPageActivity : AppCompatActivity() {
    companion object {
        private const val SHARED_PREFS_NAME = "preferences"
        private const val FIRST_LAUNCH_AFTER_INSTALL = "firstLaunchAfterInstall"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pocketpaint_landing_page)

        val toolbar = findViewById<Toolbar>(R.id.pocketpaint_toolbar_landing_page)
        setSupportActionBar(toolbar)

        val mainActivityIntent = Intent(this, MainActivity::class.java)

        val newImage = findViewById<FloatingActionButton>(R.id.pocketpaint_fab_new_image)
        newImage.setOnClickListener {
            startActivity(mainActivityIntent)
        }

        if (!BuildConfig.DEBUG) {
            val prefs = getSharedPreferences(SHARED_PREFS_NAME, 0)

            if (prefs.getBoolean(FIRST_LAUNCH_AFTER_INSTALL, true)) {
                prefs.edit().putBoolean(FIRST_LAUNCH_AFTER_INSTALL, false).apply()
                showHelpClicked()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        handleActivityResult(requestCode, resultCode)
    }

    private fun handleActivityResult(requestCode: Int, resultCode: Int) {
        when (requestCode) {
            REQUEST_CODE_INTRO -> if (resultCode == RESULT_INTRO_MW_NOT_SUPPORTED) {
                ToastFactory.makeText(
                    this,
                    R.string.pocketpaint_intro_split_screen_not_supported,
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> this.handleActivityResult(requestCode, resultCode)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_pocketpaint_main_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.pocketpaint_options_rate_us -> rateUsClicked()
            R.id.pocketpaint_options_help -> showHelpClicked()
            R.id.pocketpaint_options_about -> showAboutDialog()
            R.id.pocketpaint_options_feedback -> sendFeedback()
            else -> return false
        }
        return true
    }

    private fun rateUsClicked() {
        val applicationId = "org.catrobat.paintroid"
        PlayStore().openPlayStore(this, applicationId)
    }

    private fun showHelpClicked() {
        val intent = Intent(applicationContext, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, REQUEST_CODE_INTRO)
    }

    private fun showAboutDialog() {
        val about = AboutDialog()
        about.show(this.supportFragmentManager, ABOUT_DIALOG_FRAGMENT_TAG)
    }

    private fun sendFeedback() {
        val intent = Intent(Intent.ACTION_SENDTO)
        val data = Uri.parse("mailto:support-paintroid@catrobat.org")
        intent.data = data
        startActivity(intent)
    }

    fun getVersionCode(): String = runCatching {
        packageManager.getPackageInfo(packageName, 0).versionName
    }.getOrDefault("")
}
