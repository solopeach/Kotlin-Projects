Roni Wu <br/>
20891621 yy8wu <br/>
Kotlin plugin 231-1.8.21-IJ8770.65 <br/>
Windows 10 Pro <br/>

The emulator used is a Pixel C tablet AVD with API 31 (Pixel C API 31 2). <br/>
The application uses Android API 32 Platform. <br/>

Code for a custom ScrollView was used from this Github link: https://gist.github.com/chittaranjan-khuntia/42d5429ac37b7aea3cb22fb51c8729b4. 
This link was also posted on Piazza.<br/>

Sample code from the CS349 class repository was used. <br/>

Notes about the application:<br/>
1) Strokes do not scale properly when orientation is changed but they are retained.
2) Data saving on application exit/close was not implemented.
3) Zoom/Pan: zoom is with 2 fingers (pinch), pan is with one finger and the user must select the 'PAN' button from the toolbar.
4) Switching orientations resets the document position. Pressing 'SCROLL' button in Landscape mode also resets it.
5) Scrolling is only available on landscape orientation and there is a 'SCROLL' button that is only enabled in Landscape mode. To pan in Landscape mode, press the 'PAN' button.
6) Pan is the default when app loads in Portrait mode, Scroll is the default when loading in on Landscape mode. Switching orientations maintains the current feature.
7) Erase is stroke-based as in it checks if the stroke intersects any of the draw/highlight strokes.