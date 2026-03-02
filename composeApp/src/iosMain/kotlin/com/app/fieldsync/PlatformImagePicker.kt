package com.app.fieldsync

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.interop.LocalUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleActionSheet
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformImagePicker(
    onImagePicked: (ByteArray?) -> Unit, onDismiss: () -> Unit
) {
    val viewController = LocalUIViewController.current
    val imagePicker = remember { UIImagePickerController() }

    val delegate = remember {
        object : NSObject(), UIImagePickerControllerDelegateProtocol,
            UINavigationControllerDelegateProtocol {
            override fun imagePickerController(
                picker: UIImagePickerController, didFinishPickingMediaWithInfo: Map<Any?, *>
            ) {
                val image =
                    didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
                val bytes = image?.let {
                    val imageData = UIImageJPEGRepresentation(it, 0.8)
                    imageData?.let { data ->
                        val length = data.length.toInt()
                        val bytes = ByteArray(length)
                        memcpy(bytes.refTo(0), data.bytes, length.toULong())
                        bytes
                    }
                }
                onImagePicked(bytes)
                picker.dismissViewControllerAnimated(true, null)
                onDismiss()
            }

            override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                picker.dismissViewControllerAnimated(true, null)
                onDismiss()
            }
        }
    }

    LaunchedEffect(Unit) {
        val alertController = UIAlertController.alertControllerWithTitle(
            title = "Select Image Source",
            message = "Choose between Camera and Gallery",
            preferredStyle = UIAlertControllerStyleActionSheet
        )

        val cameraAction = UIAlertAction.actionWithTitle(
            title = "Camera", style = UIAlertActionStyleDefault, handler = {
                if (UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)) {
                    imagePicker.sourceType =
                        UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
                    imagePicker.delegate = delegate
                    viewController.presentViewController(imagePicker, true, null)
                }
            })

        val galleryAction = UIAlertAction.actionWithTitle(
            title = "Gallery", style = UIAlertActionStyleDefault, handler = {
                imagePicker.sourceType =
                    UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
                imagePicker.delegate = delegate
                viewController.presentViewController(imagePicker, true, null)
            })

        val cancelAction = UIAlertAction.actionWithTitle(
            title = "Cancel", style = UIAlertActionStyleCancel, handler = { onDismiss() })

        alertController.addAction(cameraAction)
        alertController.addAction(galleryAction)
        alertController.addAction(cancelAction)

        viewController.presentViewController(alertController, true, null)
    }
}
