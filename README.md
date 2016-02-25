# Touch

**Now we use a custom View `ViewFinder`**

Pan and zoom are completed, ~~rotation is not ()~~ **is completed**.

~~Getting the scaled and panned image that matches what the user is looking at, poses an extra problem when the image is rotated, because, depending on the rotation, a completely different part of the image should be saved. This is because the user can translate and rotate the image even outside of the viewfinder, thus making complex to reconstruct the result to be saved.~~
**FIXED!**

Need to be completed:
 - ~~save image correctly if rotated,~~ **FIXED!**
 - ~~fix bug save image if panned (in some cases)~~ **FIXED!**
 - ~~make sure different gestures (pan/zoom/rotate) don’t conflict,~~ **FIXED!**
 - ~~rotation of the image on the center of the image itself (now rotates on the center of the ImageView),~~ ~~**FIXED!**~~ **UPDATED: it's better if image rotates around center of the viewfinder, reverting to original**
 - ~~initial setup of the image depending on dimensions,~~ **DONE!**
 - ~~memory management,~~ **How to apply the matrix and save the bitmap without loading it in memory? Apparenty no solution for this, possible OOM. Instead of loading in memory two bitmpas at the same time we could wait for the user to finish and only then do the processing**
 - ~~1819x1382 pixels aspect ratio.~~ **DONE!**
