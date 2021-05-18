/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.paintroid

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.TooltipCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.catrobat.paintroid.command.CommandFactory
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.CommandManager.CommandListener
import org.catrobat.paintroid.command.implementation.AsyncCommandManager
import org.catrobat.paintroid.command.implementation.DefaultCommandFactory
import org.catrobat.paintroid.command.implementation.DefaultCommandManager
import org.catrobat.paintroid.common.CommonFactory
import org.catrobat.paintroid.common.Constants
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.contract.MainActivityContracts
import org.catrobat.paintroid.contract.MainActivityContracts.MainView
import org.catrobat.paintroid.controller.DefaultToolController
import org.catrobat.paintroid.iotasks.BitmapReturnValue
import org.catrobat.paintroid.iotasks.OpenRasterFileFormatConversion
import org.catrobat.paintroid.listener.PresenterColorPickedListener
import org.catrobat.paintroid.model.LayerModel
import org.catrobat.paintroid.model.MainActivityModel
import org.catrobat.paintroid.presenter.LayerPresenter
import org.catrobat.paintroid.presenter.MainActivityPresenter
import org.catrobat.paintroid.tools.*
import org.catrobat.paintroid.tools.implementation.*
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.catrobat.paintroid.ui.*
import org.catrobat.paintroid.ui.dragndrop.DragAndDropListView
import org.catrobat.paintroid.ui.tools.DefaultToolOptionsViewController
import org.catrobat.paintroid.ui.viewholder.*
import java.io.File
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity(), MainView, CommandListener {

    @VisibleForTesting
    lateinit var model: MainActivityContracts.Model
    @VisibleForTesting
    lateinit var perspective: Perspective
    @VisibleForTesting
    lateinit var workspace: Workspace
    @VisibleForTesting
    lateinit var layerModel: LayerContracts.Model
    @VisibleForTesting
    lateinit var commandManager: CommandManager
    @VisibleForTesting
    lateinit var toolPaint: ToolPaint
    @VisibleForTesting
    lateinit var toolReference: ToolReference
    @VisibleForTesting
    lateinit var toolOptionsViewController: ToolOptionsViewController

    private lateinit var layerPresenter: LayerPresenter
    private lateinit var drawingSurface: DrawingSurface
    private lateinit var presenter: MainActivityContracts.Presenter
    private lateinit var drawerLayoutViewHolder: DrawerLayoutViewHolder
    private lateinit var keyboardListener: KeyboardListener
    private lateinit var appFragment: PaintroidApplicationFragment
    private lateinit var defaultToolController: DefaultToolController
    private lateinit var bottomNavigationViewHolder: BottomNavigationViewHolder
    private lateinit var commandFactory: CommandFactory
    private var deferredRequestPermissionsResult: Runnable? = null

    companion object {
        const val TAG = "MainActivity"
        private const val IS_FULLSCREEN_KEY = "isFullscreen"
        private const val IS_SAVED_KEY = "isSaved"
        private const val IS_OPENED_FROM_CATROID_KEY = "isOpenedFromCatroid"
        private const val WAS_INITIAL_ANIMATION_PLAYED = "wasInitialAnimationPlayed"
        private const val SAVED_PICTURE_URI_KEY = "savedPictureUri"
        private const val CAMERA_IMAGE_URI_KEY = "cameraImageUri"
        private const val APP_FRAGMENT_KEY = "customActivityState"
    }

    override fun getPresenter(): MainActivityContracts.Presenter {
        return presenter
    }

    override fun getDisplayMetrics(): DisplayMetrics {
        return resources.displayMetrics
    }

    override fun onResume() {
        super.onResume()
        deferredRequestPermissionsResult?.let { result ->
            val runnable: Runnable = result
            deferredRequestPermissionsResult = null
            runnable.run()
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.PocketPaintTheme)
        super.onCreate(savedInstanceState)
        getAppFragment()
        PaintroidApplication.cacheDir = cacheDir
        setContentView(R.layout.activity_pocketpaint_main)
        onCreateGlobals()
        onCreateMainView()
        onCreateLayerMenu()
        onCreateDrawingSurface()
        presenter.onCreateTool()
        val receivedIntent = intent
        val receivedAction = receivedIntent.action
        val receivedType = receivedIntent.type
        if (receivedAction != null && receivedType != null && (receivedAction == Intent.ACTION_SEND || receivedAction == Intent.ACTION_EDIT || receivedAction == Intent.ACTION_VIEW) && (receivedType.startsWith("image/") || receivedType.startsWith("application/"))) {
            var receivedUri = receivedIntent
                    .getParcelableExtra<Uri>(Intent.EXTRA_STREAM)

            receivedUri = receivedUri ?: receivedIntent.data

            val mimeType: String? = if (receivedUri.scheme == ContentResolver.SCHEME_CONTENT) {
                contentResolver.getType(receivedUri)
            } else {
                val fileExtension = MimeTypeMap.getFileExtensionFromUrl(receivedUri.toString())
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase(Locale.US))
            }

            if (receivedUri != null) {
                try {
                    if (mimeType.equals("application/zip") || mimeType.equals("application/octet-stream")) {
                        OpenRasterFileFormatConversion.importOraFile(contentResolver, receivedUri, applicationContext).bitmapList?.let { bitmapList ->
                            commandManager.setInitialStateCommand(commandFactory.createInitCommand(bitmapList))
                        }
                    } else {
                        FileIO.filename = "image"
                        FileIO.getBitmapFromUri(contentResolver, receivedUri, applicationContext)?.let { receivedBitmap ->
                            commandManager.setInitialStateCommand(commandFactory.createInitCommand(receivedBitmap))
                        }
                    }
                } catch (e: IOException) {
                    Log.e("Can not read", "Unable to retrieve Bitmap from Uri")
                }
            }
            commandManager.reset()
            model.savedPictureUri = null
            model.cameraImageUri = null
            workspace.resetPerspective()
            presenter.initializeFromCleanState(null, null)
        } else if (savedInstanceState == null) {
            val intent = intent
            val picturePath = intent.getStringExtra(Constants.PAINTROID_PICTURE_PATH)
            val pictureName = intent.getStringExtra(Constants.PAINTROID_PICTURE_NAME)
            presenter.initializeFromCleanState(picturePath, pictureName)
        } else {
            val isFullscreen = savedInstanceState.getBoolean(IS_FULLSCREEN_KEY, false)
            val isSaved = savedInstanceState.getBoolean(IS_SAVED_KEY, false)
            val isOpenedFromCatroid = savedInstanceState.getBoolean(IS_OPENED_FROM_CATROID_KEY, false)
            val wasInitialAnimationPlayed = savedInstanceState.getBoolean(WAS_INITIAL_ANIMATION_PLAYED, false)
            val savedPictureUri = savedInstanceState.getParcelable<Uri>(SAVED_PICTURE_URI_KEY)
            val cameraImageUri = savedInstanceState.getParcelable<Uri>(CAMERA_IMAGE_URI_KEY)
            presenter.restoreState(isFullscreen, isSaved, isOpenedFromCatroid,
                    wasInitialAnimationPlayed, savedPictureUri, cameraImageUri)
        }
        commandManager.addCommandListener(this)
        presenter.finishInitialize()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_pocketpaint_more_options, menu)
        presenter.removeMoreOptionsItems(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.pocketpaint_options_export -> presenter.saveCopyClicked(true)
            R.id.pocketpaint_options_save_image -> presenter.saveImageClicked()
            R.id.pocketpaint_options_save_duplicate -> presenter.saveCopyClicked(false)
            R.id.pocketpaint_options_open_image -> presenter.loadImageClicked()
            R.id.pocketpaint_options_new_image -> presenter.newImageClicked()
            R.id.pocketpaint_options_discard_image -> presenter.discardImageClicked()
            R.id.pocketpaint_options_fullscreen_mode -> presenter.enterFullscreenClicked()
            R.id.pocketpaint_options_rate_us -> presenter.rateUsClicked()
            R.id.pocketpaint_options_help -> presenter.showHelpClicked()
            R.id.pocketpaint_options_about -> presenter.showAboutClicked()
            android.R.id.home -> presenter.backToPocketCodeClicked()
            R.id.pocketpaint_share_image_button -> presenter.shareImageClicked()
            R.id.pocketpaint_options_feedback -> presenter.sendFeedback()
            else -> return false
        }
        return true
    }

    private fun getAppFragment() {
        supportFragmentManager.findFragmentByTag(APP_FRAGMENT_KEY)?.let { fragment ->
            appFragment = fragment as PaintroidApplicationFragment
        }
        if (!this::appFragment.isInitialized) {
            appFragment = PaintroidApplicationFragment()
            supportFragmentManager.beginTransaction().add(appFragment, APP_FRAGMENT_KEY).commit()
        }
    }

    private fun onCreateGlobals() {
        appFragment.layerModel = appFragment.layerModel ?: LayerModel()
        layerModel = appFragment.layerModel
        commandFactory = DefaultCommandFactory()

        if (appFragment.commandManager == null) {
            val metrics = resources.displayMetrics
            val synchronousCommandManager: CommandManager = DefaultCommandManager(CommonFactory(), layerModel)
            commandManager = AsyncCommandManager(synchronousCommandManager, layerModel)
            val initCommand = commandFactory.createInitCommand(metrics.widthPixels, metrics.heightPixels)
            commandManager.setInitialStateCommand(initCommand)
            commandManager.reset()
            appFragment.commandManager = commandManager
        } else {
            commandManager = appFragment.commandManager
        }
        appFragment.toolPaint = appFragment.toolPaint ?: DefaultToolPaint(applicationContext)
        toolPaint = appFragment.toolPaint
        appFragment.currentTool = appFragment.currentTool ?: DefaultToolReference()
        toolReference = appFragment.currentTool
    }

    private fun onCreateMainView() {
        val context: Context = this
        val drawerLayout = findViewById<DrawerLayout>(R.id.pocketpaint_drawer_layout)
        val topBarLayout = findViewById<ViewGroup>(R.id.pocketpaint_layout_top_bar)
        val bottomBarLayout = findViewById<View>(R.id.pocketpaint_main_bottom_bar)
        val bottomNavigationView = findViewById<View>(R.id.pocketpaint_main_bottom_navigation)
        toolOptionsViewController = DefaultToolOptionsViewController(this)
        drawerLayoutViewHolder = DrawerLayoutViewHolder(drawerLayout)
        val topBarViewHolder = TopBarViewHolder(topBarLayout)
        val bottomBarViewHolder = BottomBarViewHolder(bottomBarLayout)
        bottomNavigationViewHolder = BottomNavigationViewHolder(bottomNavigationView, resources.configuration.orientation, applicationContext)
        perspective = Perspective(layerModel.width, layerModel.height)
        val listener = DefaultWorkspace.Listener { drawingSurface.refreshDrawingSurface() }
        workspace = DefaultWorkspace(layerModel, perspective, listener)
        model = MainActivityModel()
        defaultToolController = DefaultToolController(toolReference, toolOptionsViewController,
                DefaultToolFactory(), commandManager, workspace, toolPaint, DefaultContextCallback(context))
        val preferences = UserPreferences(getPreferences(MODE_PRIVATE))
        presenter = MainActivityPresenter(this, this, model, workspace,
                MainActivityNavigator(this, toolReference), MainActivityInteractor(), topBarViewHolder, bottomBarViewHolder, drawerLayoutViewHolder,
                bottomNavigationViewHolder, DefaultCommandFactory(), commandManager, perspective, defaultToolController, preferences, context)
        defaultToolController.setOnColorPickedListener(PresenterColorPickedListener(presenter))
        keyboardListener = KeyboardListener(drawerLayout)
        setTopBarListeners(topBarViewHolder)
        setBottomBarListeners(bottomBarViewHolder)
        setBottomNavigationListeners(bottomNavigationViewHolder)
        setActionBarToolTips(topBarViewHolder, context)
    }

    private fun onCreateLayerMenu() {
        val layerLayout = findViewById<ViewGroup>(R.id.pocketpaint_layer_side_nav_menu)
        val layerListView = findViewById<DragAndDropListView>(R.id.pocketpaint_layer_side_nav_list)
        val layerMenuViewHolder = LayerMenuViewHolder(layerLayout)
        val layerNavigator = LayerNavigator(applicationContext)
        layerPresenter = LayerPresenter(layerModel, layerListView, layerMenuViewHolder,
                commandManager, DefaultCommandFactory(), layerNavigator)
        val layerAdapter = LayerAdapter(layerPresenter)
        presenter.setLayerAdapter(layerAdapter)
        layerPresenter.setAdapter(layerAdapter)
        layerListView.setPresenter(layerPresenter)
        layerListView.adapter = layerAdapter
        layerPresenter.refreshLayerMenuViewHolder()
        setLayerMenuListeners(layerMenuViewHolder)
    }

    private fun onCreateDrawingSurface() {
        drawingSurface = findViewById(R.id.pocketpaint_drawing_surface_view)
        drawingSurface.setArguments(layerModel, perspective, toolReference, toolOptionsViewController)
        layerPresenter.setDrawingSurface(drawingSurface)
        appFragment.perspective = perspective
        layerPresenter.setDefaultToolController(defaultToolController)
        layerPresenter.setbottomNavigationViewHolder(bottomNavigationViewHolder)
    }

    private fun setLayerMenuListeners(layerMenuViewHolder: LayerMenuViewHolder) {
        layerMenuViewHolder.layerAddButton.setOnClickListener { layerPresenter.addLayer() }
        layerMenuViewHolder.layerDeleteButton.setOnClickListener { layerPresenter.removeLayer() }
    }

    private fun setActionBarToolTips(topBar: TopBarViewHolder, context: Context) {
        TooltipCompat.setTooltipText(topBar.undoButton, context.getString(R.string.button_undo))
        TooltipCompat.setTooltipText(topBar.redoButton, context.getString(R.string.button_redo))
    }

    private fun setTopBarListeners(topBar: TopBarViewHolder) {
        topBar.undoButton.setOnClickListener { presenter.undoClicked() }
        topBar.redoButton.setOnClickListener { presenter.redoClicked() }
        topBar.checkmarkButton.setOnClickListener {
            val tool = toolReference.get() as BaseToolWithShape
            tool.onClickOnButton()
        }
    }

    private fun setBottomBarListeners(viewHolder: BottomBarViewHolder) {
        val toolTypes = ToolType.values()
        for (type in toolTypes) {
            val toolButton = viewHolder.layout.findViewById<View>(type.toolButtonID)
                    ?: continue
            toolButton.setOnClickListener { presenter.toolClicked(type) }
        }
    }

    private fun setBottomNavigationListeners(viewHolder: BottomNavigationViewHolder) {
        viewHolder.bottomNavigationView.setOnNavigationItemSelectedListener(
                BottomNavigationView.OnNavigationItemSelectedListener { item ->
                    when(item.itemId) {
                        R.id.action_tools -> presenter.actionToolsClicked()
                        R.id.action_current_tool -> presenter.actionCurrentToolClicked()
                        R.id.action_color_picker -> presenter.showColorPickerClicked()
                        R.id.action_layers -> presenter.showLayerMenuClicked()
                        else -> return@OnNavigationItemSelectedListener false
                    }
                    true
                })
    }

    override fun initializeActionBar(isOpenedFromCatroid: Boolean) {
        val toolbar = findViewById<Toolbar>(R.id.pocketpaint_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(!isOpenedFromCatroid)
            setDisplayHomeAsUpEnabled(isOpenedFromCatroid)
            setHomeButtonEnabled(true)
            setDisplayShowHomeEnabled(false)
        }
    }

    override fun commandPostExecute() {
        if (!isFinishing) {
            layerPresenter.invalidate()
            presenter.onCommandPostExecute()
        }
    }

    override fun onDestroy() {
        commandManager.removeCommandListener(this)
        if (isFinishing) {
            commandManager.shutdown()
            appFragment.currentTool = null
            appFragment.commandManager = null
            appFragment.layerModel = null
        }
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        with(outState) {
            putBoolean(IS_FULLSCREEN_KEY, model.isFullscreen)
            putBoolean(IS_SAVED_KEY, model.isSaved)
            putBoolean(IS_OPENED_FROM_CATROID_KEY, model.isOpenedFromCatroid)
            putBoolean(WAS_INITIAL_ANIMATION_PLAYED, model.wasInitialAnimationPlayed())
            putParcelable(SAVED_PICTURE_URI_KEY, model.savedPictureUri)
            putParcelable(CAMERA_IMAGE_URI_KEY, model.cameraImageUri)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.isStateSaved) {
            super.onBackPressed()
        } else if (!supportFragmentManager.popBackStackImmediate()) {
            presenter.onBackPressed()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.handleActivityResult(requestCode, resultCode, data)
    }

    override fun superHandleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (VERSION.SDK_INT == Build.VERSION_CODES.M) {
            deferredRequestPermissionsResult = Runnable { presenter.handleRequestPermissionsResult(requestCode, permissions, grantResults) }
        } else {
            presenter.handleRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun superHandleRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun isKeyboardShown(): Boolean {
        return keyboardListener.isSoftKeyboardVisible
    }

    override fun refreshDrawingSurface() {
        drawingSurface.refreshDrawingSurface()
    }

    override fun enterFullscreen() {
        drawingSurface.disableAutoScroll()
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
    }

    override fun exitFullscreen() {
        drawingSurface.enableAutoScroll()
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun getUriFromFile(file: File): Uri {
        return Uri.fromFile(file)
    }

    override fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
        if (inputMethodManager != null) {
            val rootView = window.decorView.rootView
            inputMethodManager.hideSoftInputFromWindow(rootView.windowToken, 0)
        }
    }
}