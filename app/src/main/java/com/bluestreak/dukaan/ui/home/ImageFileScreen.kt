package com.bluestreak.dukaan.ui.home

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bluestreak.dukaan.DukaanTopAppBar
import com.bluestreak.dukaan.R
import com.bluestreak.dukaan.ui.navigation.NavigationDestination
import com.bluestreak.dukaan.ui.theme.DukaanTheme
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel

object ImageEntryDestination : NavigationDestination {
    override val route = "image_entry"
    override val titleRes = R.string.image_upload_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageFileScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
) {
    Scaffold(
        topBar = {
            DukaanTopAppBar(
                title = stringResource(ImageEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
            )
        }
    ) { innerPadding ->
        ImageEntryBody(
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun ImageEntryBody(
    modifier: Modifier = Modifier
){
    val context = LocalContext.current

    var fileName  by remember { mutableStateOf("group_img_0.png") }

    var imageUri: Uri? by remember { mutableStateOf(null) }

    val folder = createDukaanDirectory(context)

    // Registers a photo picker activity launcher in single-select mode.
    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the photo picker.
        if (uri != null) {
            //Log.d("Dukaan", "Selected URI: $uri")
            imageUri = uri

        } else {
            //Log.d("Dukaan", "No media selected")
        }
    }

    var showNotification by rememberSaveable { mutableStateOf(false) }
    var statusMessage by rememberSaveable { mutableStateOf("Failed") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(top = 100.dp)
            .fillMaxWidth()
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {

            IconButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    // Launch the photo picker and let the user choose only images.
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            ) {
                Row(horizontalArrangement = Arrangement.Center) {
                    Text(
                        text = "Add image"
                    )
                    Icon(Icons.Default.Add, contentDescription = "Select Image")
                }
            }
        }

        OutlinedTextField(
            value = fileName,
            onValueChange = { fileName = it },
            label = { Text(stringResource(R.string.file_name_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.padding(8.dp),
            enabled = true,
            singleLine = true
        )
        if(showNotification){
            NotificationDialog(
                statusMessage = statusMessage,
                onDismissRequest = { showNotification = false }
            )
        }
        Button(
            onClick = {
                val status = copyToDukaanDirectory(context, folder, imageUri, fileName)
                //Log.d("Dukaan",status.toString())
                if(status) statusMessage =  "Success"
                showNotification = true

                      },
            enabled = imageUri != null,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = stringResource(R.string.save_action))
        }

        FileListScreen()
    }
}
@Composable
fun NotificationDialog(
    statusMessage: String = stringResource(R.string.done),
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = statusMessage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Text(stringResource(R.string.ok))
                    }

                }
            }
        }
    }
}

@Composable
fun FileListScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val fileNames = remember { getFileList(context) }

    if (fileNames.isNotEmpty()) {
        LazyColumn(
            modifier = modifier,
        ) {
            items(fileNames) { fileName ->
                Text(text = fileName)
            }
        }
    } else {
        Text(text = "No files found in app directory.")
    }
}

@Preview(showBackground = true)
@Composable
fun ImageFileScreenPreview(modifier: Modifier = Modifier) {
    DukaanTheme {
        ImageEntryBody()
    }
}

fun createDukaanDirectory(context: Context): File{
    val f = File(context.filesDir, "DukaanData")
    if(!f.exists()) {
        //folder not created. creating new directory
        f.mkdir()
    } else {
        //folder already created
    }
    //Log.d("DUKAAN",f.name)
    return f
}

fun copyToDukaanDirectory(context: Context, tgtFolder: File, uri: Uri?, fileName: String): Boolean {
    var copySuccess = false
    if(uri != null && fileName.isNotBlank()){
        val contentResolver = context.contentResolver
        val photoFile = File(tgtFolder, fileName)
        val sourceFile = File(getPathFromURI(uri!!, contentResolver))
        try {
            getMoveFileToAppFolder(sourceFile, photoFile)
            copySuccess = true
        }catch (e: IOException){
            copySuccess = false
        }
    }
    return copySuccess
}

fun getPathFromURI(contentUri: Uri?, contentResolver: ContentResolver): String? {
    var res: String? = null
    val proj = arrayOf(MediaStore.Images.Media.DATA)

    val cursor = contentResolver.query(contentUri!!, proj, null, null, null)
    if (cursor!!.moveToFirst()) {
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        res = cursor.getString(column_index)
    }
    cursor.close()
    return res
}

@Throws(IOException::class)
private fun getMoveFileToAppFolder(sourceFile: File, destFile: File) {
    if (!sourceFile.exists()) {
        return
    }
    var source: FileChannel? = null
    var destination: FileChannel? = null
    source = FileInputStream(sourceFile).getChannel()
    destination = FileOutputStream(destFile).getChannel()
    if (destination != null && source != null) {
        destination.transferFrom(source, 0, source.size())
    }
    source?.close()
    destination?.close()
}

fun getAppFilesDirectory(context: Context): File {
    return File(context.filesDir, "DukaanData")
}

fun getFileList(context: Context): List<String> {
    val appDir = getAppFilesDirectory(context)
    val files = appDir.listFiles()
    return files?.map { it.name } ?: emptyList()
}

