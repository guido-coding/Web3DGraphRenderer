let data =  
  [
    [
      {
        x: 1.1,
        y: 1.1,
        z: 0.5
      }, 
      {
        x: 3.1,
        y: 1.1,
        z: 1.1
      },
      {
        x: 4,
        y: 1.1,
        z: 2.1
      }
    ],[
      {
        x: 1.1,
        y: 3.1,
        z: 0.5
      },
      {
        x: 3.1,
        y: 3.1,
        z: 1.1
      },
      {
        x: 4.1,
        y: 3.1,
        z: 2.1
      },

    ]
  ];

export const initialSettings = {
    rotation: 0.5,
    vrot: 0.3,
    zoom: 105,
    width: 1000,
    height: 1000,
    minX: 0,
    minY: 0,
    minZ: 0,
    maxX: 10,
    maxY: 10,
    maxZ: 10,
    autoAdjustZ: true,
    scalingFactorZ: 1.0,
    showAxis: true,
    showGrid: true,
    showLabels: true,
    transparency: 150,
    yOffset: 0,
    xOffset: 0,
    zOffset: 0,
    steps: 10,
    data: data
};

export const initialInput = 
"x;1;2;3;4;5\n\
1;3;3;3;3;3\n\
2;3;3;3;3;3\n\
3;3;3;3;3;3\n\
4;3;3;3;3;3";