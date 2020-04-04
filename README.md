# BTV-model

This is a model to simulate the transmisison of Bluetongue virus by infected midge vectors through the farm population of Scotland. It is written by Paul Bessell (paul.bessell@roslin.ed.ac.uk) and published at doi:10.1038/srep38940

The model is called throuhg the main string args driver at driver.java which initiates objects of BTVModel which is the main driver.

BTVModel creates a vector of Farm objects by reading data through readWrite.java. It also reads climate data that inform parameters.

BTVModel creates a vector of Farm objects which are the basic unit of transmission. The Farm objects are defined by key instance variables which define the location and the code of the farm and each farm is initiated with a number of animals.

Transmission between farms id modelled through a Kernel (in BTVModel) but the probability of being infected is determined by properties of the Vector (in Class Bite). This can be extended to include movements between farms.

Once an animal is infected on a Farm n object of Class Animal is created.
