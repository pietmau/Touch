# Touch

This work is unfinished, please consider it as an such, I have tried to implement all the features I could in the given time rather than polish it.
The result is ugly, I apologize for the discomfort of reading it.
Needs to be cleaned (badly), refactored (very badly) and obviously all features should be implemented.

Pan and zoom are completed, rotation is nearly completed.

~~Regarding rotation I could not complete the saving of the image.~~
~~Getting the scaled and panned image that matches what the user is looking at, poses an extra problem when the image is rotated, because, depending on the rotation, a completely different part of the image should be saved. This is because the user can translate and rotate the image even outside of the viewfinder, thus making complex to reconstruct the result to be saved.~~
**\*\*FIXED\*\***

Need to be completed:
 - ~~save image correctly if rotated,~~ **\*\*FIXED\*\***
 - ~~fix bug save image if panned (in some cases)~~**\*\*FIXED\*\***
 - make sure different gestures (pan/zoom/rotate) don’t conflict,
 - rotation of the image on the center of the image itself (now rotates on the center of the ImageView),
 - initial setup of the image depending on dimensions,
 - memory management,
 - 1819x1382 pixels aspect ratio.
