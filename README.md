# Web3DGraphRenderer

## Project setup
Maven project using Spring Boot framework (Java). Application acts as REST controller accepting HTTP GET requests containing equation to render and associated settings. Application is listening to requests to /image (e.g. http://localhost:8080/image).  
The following optional arguments can be passed to the GET request (in any order):  
* equation, the equation to display using x and y as variable names.  
* rotation, rotation of the camera in the horizontal (in the x/y plane)  
* vrot, rotation of the camera in the vertical plane (angle between the x/y plane and the z axis)
* zoom, zoom of the camera
* width, width of the image to render (pixels)
* height, height of the image to render (pixels)
* minx, minimum location of the x-axis
* maxx, maximum location of the x-axis
* miny, minimum location of the y-axis
* maxy, maximum location of the y-axis
* minz, minimum location of the z-axis
* maxz, maximum location of the z-axis
* autoadjustz, auto adjust the scale of the z axis based on the values (use true or false as argument)
* scalingfactorz, scaling of z axis compared to x and y axis
* showaxis, (use true or false as argument)
* showgrid, (use true or false as argument)
* showlabel, (use true or false as argument)
* alpha, transparency value
* yoffset, displayed intercept of the grid
* xoffset, displayed intercept of the grid
* zoffset, displayed intercept of the grid
* steps, number of steps of the graph mesh
  
Returns an image file.

The web3dgraphrenderer.html file is used to submit GET request and display image.

Uses [Equation parser project](https://github.com/guido-coding/equationparser) to calculate results from entered equation.  
Uses [render3d project](https://github.com/guido-coding/render3d) to render 3D graph images.  
Install .jar files located in the lib directory in the local Maven respository and update pom.xml file. Update the address of the API server (variable apiserver) in graph3drenderer.js.


## Example
<img width="787" height="1186" alt="webrenderer" src="https://github.com/user-attachments/assets/656ef1a8-8f17-49f5-a3fe-c7b283e0915b" />
