/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.TooltipCompat
import androidx.core.widget.ContentLoadingProgressBar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.idling.CountingIdlingResource
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.catrobat.paintroid.colorpicker.ColorHistory
import org.catrobat.paintroid.command.CommandFactory
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.CommandManager.CommandListener
import org.catrobat.paintroid.command.implementation.AsyncCommandManager
import org.catrobat.paintroid.command.implementation.DefaultCommandFactory
import org.catrobat.paintroid.command.implementation.DefaultCommandManager
import org.catrobat.paintroid.command.implementation.LayerOpacityCommand
import org.catrobat.paintroid.command.serialization.CommandSerializer
import org.catrobat.paintroid.common.CommonFactory
import org.catrobat.paintroid.common.PAINTROID_PICTURE_NAME
import org.catrobat.paintroid.common.PAINTROID_PICTURE_PATH
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.contract.MainActivityContracts
import org.catrobat.paintroid.contract.MainActivityContracts.MainView
import org.catrobat.paintroid.controller.DefaultToolController
import org.catrobat.paintroid.iotasks.OpenRasterFileFormatConversion
import org.catrobat.paintroid.listener.DrawerLayoutListener
import org.catrobat.paintroid.listener.PresenterColorPickedListener
import org.catrobat.paintroid.model.LayerModel
import org.catrobat.paintroid.model.MainActivityModel
import org.catrobat.paintroid.presenter.LayerPresenter
import org.catrobat.paintroid.presenter.MainActivityPresenter
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape
import org.catrobat.paintroid.tools.implementation.ClippingTool
import org.catrobat.paintroid.tools.implementation.DefaultContextCallback
import org.catrobat.paintroid.tools.implementation.DefaultToolFactory
import org.catrobat.paintroid.tools.implementation.DefaultToolPaint
import org.catrobat.paintroid.tools.implementation.DefaultToolReference
import org.catrobat.paintroid.tools.implementation.DefaultWorkspace
import org.catrobat.paintroid.tools.implementation.LineTool
import org.catrobat.paintroid.tools.implementation.TransformTool
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.catrobat.paintroid.ui.DrawingSurface
import org.catrobat.paintroid.ui.KeyboardListener
import org.catrobat.paintroid.ui.LayerAdapter
import org.catrobat.paintroid.ui.LayerNavigator
import org.catrobat.paintroid.ui.MainActivityInteractor
import org.catrobat.paintroid.ui.MainActivityNavigator
import org.catrobat.paintroid.ui.Perspective
import org.catrobat.paintroid.ui.dragndrop.DragAndDropListView
import org.catrobat.paintroid.ui.tools.DefaultToolOptionsViewController
import org.catrobat.paintroid.ui.viewholder.BottomBarViewHolder
import org.catrobat.paintroid.ui.viewholder.BottomNavigationViewHolder
import org.catrobat.paintroid.ui.viewholder.DrawerLayoutViewHolder
import org.catrobat.paintroid.ui.viewholder.LayerMenuViewHolder
import org.catrobat.paintroid.ui.viewholder.TopBarViewHolder
import org.catrobat.paintroid.ui.zoomwindow.DefaultZoomWindowController
import java.io.File
import java.util.Locale

private const val TEMP_IMAGE_COROUTINE_DELAY_MILLI_SEC = 1000
private const val MILLI_SEC_TO_SEC = 1000
private const val TEMP_IMAGE_SAVE_INTERVAL = 60
private const val TEMP_IMAGE_IDLE_INTERVAL = 2 * TEMP_IMAGE_COROUTINE_DELAY_MILLI_SEC

class MainActivity : AppCompatActivity(), MainView, CommandListener {
    @VisibleForTesting
    lateinit var perspective: Perspective

    @VisibleForTesting
    lateinit var workspace: Workspace

    @VisibleForTesting
    lateinit var layerModel: LayerContracts.Model

    @VisibleForTesting
    lateinit var toolReference: ToolReference

    @VisibleForTesting
    lateinit var toolOptionsViewController: ToolOptionsViewController

    var idlingResource: CountingIdlingResource = CountingIdlingResource("MainIdleResource")

    @VisibleForTesting
    lateinit var zoomWindowController: DefaultZoomWindowController

    lateinit var commandManager: CommandManager
    lateinit var toolPaint: ToolPaint
    lateinit var bottomNavigationViewHolder: BottomNavigationViewHolder
    lateinit var model: MainActivityContracts.Model

    private lateinit var commandSerializer: CommandSerializer
    private lateinit var layerPresenter: LayerPresenter
    private lateinit var drawingSurface: DrawingSurface
    private lateinit var presenterMain: MainActivityContracts.Presenter
    private lateinit var drawerLayoutViewHolder: DrawerLayoutViewHolder
    private lateinit var keyboardListener: KeyboardListener
    private lateinit var appFragment: PaintroidApplicationFragment
    lateinit var defaultToolController: DefaultToolController
    private lateinit var commandFactory: CommandFactory
    private var deferredRequestPermissionsResult: Runnable? = null
    private lateinit var progressBar: ContentLoadingProgressBar

    @Volatile
    private var lastInteractionTime = System.currentTimeMillis()

    @Volatile
    private var minuteTemporaryCopiesCounter = 0

    @Volatile
    private var userInteraction = false
    private var isTemporaryFileSavingTest = false

    private val isRunningEspressoTests: Boolean by lazy {
        try {
            Class.forName("androidx.test.espresso.Espresso")
            true
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "Application is not in test mode.")
            false
        }
    }

    companion object {
        const val TAG = "MainActivity"
        private const val IS_FULLSCREEN_KEY = "isFullscreen"
        private const val IS_SAVED_KEY = "isSaved"
        private const val IS_OPENED_FROM_CATROID_KEY = "isOpenedFromCatroid"
        private const val IS_OPENED_FROM_FORMULA_EDITOR_IN_CATROID_KEY =
            "isOpenedFromFormulaEditorInCatroid"
        private const val SAVED_PICTURE_URI_KEY = "savedPictureUri"
        private const val CAMERA_IMAGE_URI_KEY = "cameraImageUri"
        private const val APP_FRAGMENT_KEY = "customActivityState"
        private const val SHARED_PREFS_NAME = "preferences"
        private const val FIRST_LAUNCH_AFTER_INSTALL = "firstLaunchAfterInstall"
    }

    override val presenter: MainActivityContracts.Presenter
        get() = presenterMain

    override val displayMetrics: DisplayMetrics
        get() = resources.displayMetrics

    override val isKeyboardShown: Boolean
        get() = keyboardListener.isSoftKeyboardVisible

    override val myContentResolver: ContentResolver
        get() = contentResolver

    override val finishing: Boolean
        get() = isFinishing

    override fun onResume() {
        super.onResume()
        deferredRequestPermissionsResult?.let { result ->
            val runnable: Runnable = result
            deferredRequestPermissionsResult = null
            runnable.run()
        }
    }

    @SuppressWarnings("TooGenericExceptionCaught")
    private fun handleIntent(receivedIntent: Intent): Boolean {
        var receivedUri = receivedIntent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)

        receivedUri = receivedUri ?: receivedIntent.data

        val mimeType: String? = if (receivedUri?.scheme == ContentResolver.SCHEME_CONTENT) {
            myContentResolver.getType(receivedUri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(receivedUri?.toString())
            MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(fileExtension.toLowerCase(Locale.US))
        }

        receivedUri ?: return true
        try {
            if (mimeType.equals("application/zip") || mimeType.equals("application/octet-stream")) {
                try {
                    val fileContent = commandSerializer.readFromFile(receivedUri)
                    commandManager.loadCommandsCatrobatImage(fileContent.commandModel)
                    presenterMain.setColorHistoryAfterLoadImage(fileContent.colorHistory)
                    return false
                } catch (e: CommandSerializer.NotCatrobatImageException) {
                    Log.e(TAG, "Image might be an ora file instead")
                    OpenRasterFileFormatConversion.mainActivity = this
                    OpenRasterFileFormatConversion.importOraFile(
                        myContentResolver,
                        receivedUri
                    ).layerList?.let { layerList ->
                        commandManager.setInitialStateCommand(
                            commandFactory.createInitCommand(
                                layerList
                            )
                        )
                    }
                    val paint = Paint()
                    val coordinate = PointF(0.0f, 0.0f)
                    paint.color = Color.TRANSPARENT
                    commandManager.addCommand(commandFactory.createPointCommand(paint, coordinate))
                    commandManager.undo()
                    commandManager.reset()
                }
            } else {
                FileIO.filename = "image"
                FileIO.getBitmapFromUri(myContentResolver, receivedUri, applicationContext)
                    ?.let { receivedBitmap ->
                        commandManager.setInitialStateCommand(
                            commandFactory.createInitCommand(
                                receivedBitmap
                            )
                        )
                    }
            }
        } catch (e: Exception) {
            Log.e("Can not read", "Unable to retrieve Bitmap from Uri")
        }
        return true
    }

    private fun validateIntent(receivedIntent: Intent): Boolean {
        val receivedAction = receivedIntent.action
        val receivedType = receivedIntent.type
        return receivedAction != null && receivedType != null && (receivedAction == Intent.ACTION_SEND || receivedAction == Intent.ACTION_EDIT || receivedAction == Intent.ACTION_VIEW) && (
            receivedType.startsWith(
                "image/"
            ) || receivedType.startsWith("application/")
            )
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
        presenterMain.onCreateTool()
        OpenRasterFileFormatConversion.mainActivity = this

        val receivedIntent = intent
        isTemporaryFileSavingTest = intent.getBooleanExtra("isTemporaryFileSavingTest", false)
        when {
            validateIntent(receivedIntent) && savedInstanceState == null -> {
                if (handleIntent(receivedIntent)) {
                    commandManager.reset()
                }
                model.savedPictureUri = null
                model.cameraImageUri = null
                workspace.resetPerspective()
                presenterMain.initializeFromCleanState(null, null)
            }
            savedInstanceState == null -> {
                val intent = intent
                val picturePath = intent.getStringExtra(PAINTROID_PICTURE_PATH)
                val pictureName = intent.getStringExtra(PAINTROID_PICTURE_NAME)
                presenterMain.initializeFromCleanState(picturePath, pictureName)

                if (!model.isOpenedFromCatroid && presenterMain.checkForTemporaryFile() && (!isRunningEspressoTests || isTemporaryFileSavingTest)) {
                    val workspaceReturnValue = presenterMain.openTemporaryFile()
                    commandManager.loadCommandsCatrobatImage(workspaceReturnValue?.commandManagerModel)
                    model.colorHistory = workspaceReturnValue?.colorHistory ?: ColorHistory()
                    model.colorHistory.colors.lastOrNull()?.let {
                        toolReference.tool?.changePaintColor(it)
                        presenterMain.setBottomNavigationColor(it)
                    }
                }
                workspace.perspective.setBitmapDimensions(layerModel.width, layerModel.height)
            }
            else -> {
                val isFullscreen = savedInstanceState.getBoolean(IS_FULLSCREEN_KEY, false)
                val isSaved = savedInstanceState.getBoolean(IS_SAVED_KEY, false)
                val isOpenedFromCatroid = savedInstanceState.getBoolean(IS_OPENED_FROM_CATROID_KEY, false)
                val isOpenedFromFormulaEditorInCatroid = savedInstanceState.getBoolean(
                    IS_OPENED_FROM_FORMULA_EDITOR_IN_CATROID_KEY, false
                )
                val savedPictureUri = savedInstanceState.getParcelable<Uri>(SAVED_PICTURE_URI_KEY)
                val cameraImageUri = savedInstanceState.getParcelable<Uri>(CAMERA_IMAGE_URI_KEY)
                presenterMain.restoreState(
                    isFullscreen, isSaved, isOpenedFromCatroid, isOpenedFromFormulaEditorInCatroid,
                    savedPictureUri, cameraImageUri
                )
            }
        }

        commandManager.addCommandListener(this)
        lastInteractionTime = System.currentTimeMillis()
        if ((!isRunningEspressoTests || isTemporaryFileSavingTest) && !model.isOpenedFromCatroid) {
            startAutoSaveCoroutine()
        }
        presenterMain.finishInitialize()

        if (!BuildConfig.DEBUG) {
            val prefs = getSharedPreferences(SHARED_PREFS_NAME, 0)

            if (prefs.getBoolean(FIRST_LAUNCH_AFTER_INSTALL, true)) {
                prefs.edit().putBoolean(FIRST_LAUNCH_AFTER_INSTALL, false).apply()
                presenterMain.showHelpClicked()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        this.window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_pocketpaint_more_options, menu)
        presenterMain.removeMoreOptionsItems(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.pocketpaint_options_export -> presenterMain.saveCopyClicked(true)
            R.id.pocketpaint_options_save_image -> presenterMain.saveImageClicked()
            R.id.pocketpaint_options_save_duplicate -> presenterMain.saveCopyClicked(false)
            R.id.pocketpaint_replace_image -> presenterMain.replaceImageClicked()
            R.id.pocketpaint_add_to_current_layer -> presenterMain.addImageToCurrentLayerClicked()
            R.id.pocketpaint_options_new_image -> presenterMain.newImageClicked()
            R.id.pocketpaint_options_discard_image -> presenterMain.discardImageClicked()
            R.id.pocketpaint_options_fullscreen_mode -> {
                perspective.mainActivity = this
                presenterMain.enterHideButtonsClicked()
            }
            R.id.pocketpaint_options_rate_us -> presenterMain.rateUsClicked()
            R.id.pocketpaint_options_help -> presenterMain.showHelpClicked()
            R.id.pocketpaint_options_about -> presenterMain.showAboutClicked()
            R.id.pocketpaint_share_image_button -> presenterMain.shareImageClicked()
            R.id.pocketpaint_options_feedback -> presenterMain.sendFeedback()
            R.id.pocketpaint_zoom_window_settings ->
                presenterMain.showZoomWindowSettingsClicked(
                    UserPreferences(getPreferences(MODE_PRIVATE))
                )
            R.id.pocketpaint_advanced_settings -> presenterMain.showAdvancedSettingsClicked()
            android.R.id.home -> presenterMain.backToPocketCodeClicked()
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
        val currentLayerModel = appFragment.layerModel ?: LayerModel()
        appFragment.layerModel = currentLayerModel
        layerModel = currentLayerModel

        commandFactory = DefaultCommandFactory()

        val currentCommandManager = appFragment.commandManager
        if (currentCommandManager == null) {
            val metrics = resources.displayMetrics
            val synchronousCommandManager: CommandManager =
                DefaultCommandManager(CommonFactory(), layerModel)
            commandManager = AsyncCommandManager(synchronousCommandManager, layerModel)
            val initCommand =
                commandFactory.createInitCommand(metrics.widthPixels, metrics.heightPixels)
            commandManager.setInitialStateCommand(initCommand)
            commandManager.reset()
            appFragment.commandManager = commandManager
        } else {
            commandManager = currentCommandManager
        }

        val currentToolPaint = appFragment.toolPaint ?: DefaultToolPaint(applicationContext)
        appFragment.toolPaint = currentToolPaint
        toolPaint = currentToolPaint

        val currentTool = appFragment.currentTool ?: DefaultToolReference()
        appFragment.currentTool = currentTool
        toolReference = currentTool
    }

    private fun onCreateMainView() {
        val context: Context = this
        val drawerLayout = findViewById<DrawerLayout>(R.id.pocketpaint_drawer_layout)
        val topBarLayout = findViewById<ViewGroup>(R.id.pocketpaint_layout_top_bar)
        val bottomBarLayout = findViewById<View>(R.id.pocketpaint_main_bottom_bar)
        val bottomNavigationView = findViewById<View>(R.id.pocketpaint_main_bottom_navigation)
        toolOptionsViewController = DefaultToolOptionsViewController(this, idlingResource)
        drawerLayoutViewHolder = DrawerLayoutViewHolder(drawerLayout)
        val topBarViewHolder = TopBarViewHolder(topBarLayout)
        val bottomBarViewHolder = BottomBarViewHolder(bottomBarLayout)
        bottomNavigationViewHolder = BottomNavigationViewHolder(
            bottomNavigationView,
            resources.configuration.orientation,
            applicationContext
        )
        perspective = Perspective(layerModel.width, layerModel.height)
        val listener = DefaultWorkspace.Listener { drawingSurface.refreshDrawingSurface() }
        model = MainActivityModel()
        workspace = DefaultWorkspace(
            layerModel,
            perspective,
            listener,
        )
        commandSerializer = CommandSerializer(this, commandManager, model)
        model = MainActivityModel()
        zoomWindowController = DefaultZoomWindowController(
            this,
            layerModel,
            workspace,
            toolReference,
            UserPreferences(getPreferences(MODE_PRIVATE))
        )
        model = MainActivityModel()
        defaultToolController = DefaultToolController(
            toolReference,
            toolOptionsViewController,
            DefaultToolFactory(this),
            commandManager,
            workspace,
            idlingResource,
            toolPaint,
            DefaultContextCallback(context)
        )
        val preferences = UserPreferences(getPreferences(MODE_PRIVATE))
        val navigator = MainActivityNavigator(this, toolReference)
        presenterMain = MainActivityPresenter(
            this,
            this,
            model,
            workspace,
            MainActivityNavigator(this, toolReference),
            MainActivityInteractor(idlingResource),
            topBarViewHolder,
            bottomBarViewHolder,
            drawerLayoutViewHolder,
            bottomNavigationViewHolder,
            DefaultCommandFactory(),
            commandManager,
            defaultToolController,
            preferences,
            idlingResource,
            context,
            filesDir,
            commandSerializer
        )
        FileIO.navigator = navigator
        defaultToolController.setOnColorPickedListener(PresenterColorPickedListener(presenterMain))
        keyboardListener = KeyboardListener(drawerLayout)
        setTopBarListeners(topBarViewHolder)
        setBottomBarListeners(bottomBarViewHolder)
        setBottomNavigationListeners(bottomNavigationViewHolder)
        setActionBarToolTips(topBarViewHolder, context)
        progressBar = findViewById(R.id.pocketpaint_content_loading_progress_bar)
    }

    private fun onCreateLayerMenu() {
        val layerLayout = findViewById<NavigationView>(R.id.pocketpaint_nav_view_layer)
        val drawerLayout = findViewById<DrawerLayout>(R.id.pocketpaint_drawer_layout)
        val layerListView = findViewById<DragAndDropListView>(R.id.pocketpaint_layer_side_nav_list)
        val layerMenuViewHolder = LayerMenuViewHolder(layerLayout)
        val layerNavigator = LayerNavigator(applicationContext)
        layerPresenter = LayerPresenter(
            layerModel, layerListView, layerMenuViewHolder,
            commandManager, DefaultCommandFactory(), layerNavigator
        )
        val layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        layerListView.layoutManager = layoutManager
        layerListView.manager = layoutManager
        val layerAdapter = LayerAdapter(layerPresenter, this)
        layerListView.setLayerAdapter(layerAdapter)
        presenterMain.setLayerAdapter(layerAdapter)
        layerPresenter.setAdapter(layerAdapter)
        layerListView.setPresenter(layerPresenter)
        layerListView.adapter = layerAdapter
        layerPresenter.refreshLayerMenuViewHolder()
        layerPresenter.disableVisibilityAndOpacityButtons()
        setLayerMenuListeners(layerMenuViewHolder)
        val drawerLayoutListener = DrawerLayoutListener(this, layerPresenter)
        drawerLayout.addDrawerListener(drawerLayoutListener)
    }

    private fun onCreateDrawingSurface() {
        drawingSurface = findViewById(R.id.pocketpaint_drawing_surface_view)
        drawingSurface.setArguments(
            layerModel,
            perspective,
            toolReference,
            idlingResource,
            supportFragmentManager,
            toolOptionsViewController,
            drawerLayoutViewHolder,
            zoomWindowController,
            UserPreferences(getPreferences(MODE_PRIVATE))
        )
        layerPresenter.setDrawingSurface(drawingSurface)
        appFragment.perspective = perspective
        layerPresenter.setDefaultToolController(defaultToolController)
        layerPresenter.setBottomNavigationViewHolder(bottomNavigationViewHolder)
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
        topBar.undoButton.setOnClickListener { presenterMain.undoClicked() }
        topBar.redoButton.setOnClickListener { presenterMain.redoClicked() }
        topBar.checkmarkButton.setOnClickListener {
            if (toolReference.tool?.toolType?.name.equals(ToolType.TRANSFORM.name)) {
                (toolReference.tool as TransformTool).checkMarkClicked = true
                val tool = toolReference.tool as BaseToolWithShape?
                tool?.onClickOnButton()
            } else if (toolReference.tool?.toolType?.name.equals(ToolType.CLIP.name)) {
                val tool = toolReference.tool as ClippingTool?
                tool?.onClickOnButton()
            } else {
                val tool = toolReference.tool as BaseToolWithShape?
                tool?.onClickOnButton()
            }
        }
        topBar.plusButton.setOnClickListener {
            val tool = toolReference.tool as LineTool
            tool.onClickOnPlus()
        }
        LineTool.topBarViewHolder = topBar
    }

    private fun setBottomBarListeners(viewHolder: BottomBarViewHolder) {
        val toolTypes = ToolType.values()
        for (type in toolTypes) {
            val toolButton = viewHolder.layout.findViewById<View>(type.toolButtonID) ?: continue
            toolButton.setOnClickListener { presenterMain.toolClicked(type) }
        }
    }

    private fun setBottomNavigationListeners(viewHolder: BottomNavigationViewHolder) {
        viewHolder.bottomNavigationView.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.action_tools -> presenterMain.actionToolsClicked()
                    R.id.action_current_tool -> presenterMain.actionCurrentToolClicked()
                    R.id.action_color_picker -> presenterMain.showColorPickerClicked()
                    R.id.action_layers -> presenterMain.showLayerMenuClicked()
                    else -> return@OnNavigationItemSelectedListener false
                }
                true
            }
        )
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
        if (!finishing) {
            if (commandManager.lastExecutedCommand !is LayerOpacityCommand) {
                layerPresenter.invalidate()
            }
            presenterMain.onCommandPostExecute()
        }
    }

    override fun onDestroy() {
        commandManager.removeCommandListener(this)
        if (finishing) {
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
            putBoolean(
                IS_OPENED_FROM_FORMULA_EDITOR_IN_CATROID_KEY,
                model.isOpenedFromFormulaEditorInCatroid
            )
            putParcelable(SAVED_PICTURE_URI_KEY, model.savedPictureUri)
            putParcelable(CAMERA_IMAGE_URI_KEY, model.cameraImageUri)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.isStateSaved) {
            super.onBackPressed()
        } else if (!supportFragmentManager.popBackStackImmediate()) {
            presenterMain.onBackPressed()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenterMain.handleActivityResult(requestCode, resultCode, data)
    }

    override fun superHandleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (VERSION.SDK_INT == Build.VERSION_CODES.M) {
            deferredRequestPermissionsResult = Runnable {
                presenterMain.handleRequestPermissionsResult(
                    requestCode,
                    permissions,
                    grantResults
                )
            }
        } else {
            presenterMain.handleRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun superHandleRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun refreshDrawingSurface() {
        drawingSurface.refreshDrawingSurface()
    }

    override fun enterHideButtons() {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        }
    }

    override fun exitHideButtons() {
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun getUriFromFile(file: File): Uri = Uri.fromFile(file)

    override fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
        if (inputMethodManager != null) {
            val rootView = window.decorView.rootView
            inputMethodManager.hideSoftInputFromWindow(rootView.windowToken, 0)
        }
    }

    override fun showContentLoadingProgressBar() {
        progressBar.show()
    }

    override fun hideContentLoadingProgressBar() {
        progressBar.hide()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        lastInteractionTime = System.currentTimeMillis()
        userInteraction = true
    }

    @Synchronized
    private fun addToMinuteTemporaryCopiesCounter(seconds: Int) {
        this.minuteTemporaryCopiesCounter += seconds
    }

    private fun startAutoSaveCoroutine() {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(TEMP_IMAGE_COROUTINE_DELAY_MILLI_SEC.toLong())
                addToMinuteTemporaryCopiesCounter(TEMP_IMAGE_COROUTINE_DELAY_MILLI_SEC / MILLI_SEC_TO_SEC)
                if ((System.currentTimeMillis() - lastInteractionTime >= TEMP_IMAGE_IDLE_INTERVAL || minuteTemporaryCopiesCounter >= TEMP_IMAGE_SAVE_INTERVAL) && userInteraction) {
                    presenterMain.saveNewTemporaryImage()
                    minuteTemporaryCopiesCounter = 0
                    userInteraction = false
                }
            }
        }
    }

    fun getVersionCode(): String = runCatching {
        packageManager.getPackageInfo(packageName, 0).versionName
    }.getOrDefault("")
}
