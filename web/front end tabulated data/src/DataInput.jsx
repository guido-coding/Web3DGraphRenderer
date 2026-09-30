import {useState} from 'react';
import './DataInput.css'

/*
const data = [[1,2,3],[4,5,6],[7,8,9]];
const xvalues = [1,2,3];
const yvalues = [1,2,3];

const initialData = {
    xvalues: xvalues,
    yvalues: yvalues,
    data: data
};
*/

function initializeInput() {

    function getValue(x, y) {
        return 0.1*y*y-0.1*x*x+0.1*x*y+0.1;
    }

    let xvalues = [0,1,2,3,4,5,6,7,8,9,10];
    let yvalues = [0,1,2,3,4,5,6,7,8,9,10];
    let data = [];

    for (let y=0; y<yvalues.length; y++) {
        let row = [];
        for (let x=0; x<xvalues.length; x++) {
            row.push(getValue(x,y));
        }
        data.push(row);
    }

    return {
        xvalues: xvalues,
        yvalues: yvalues,
        data: data
    };
}

function transformGraphData(graphData) {
    let data = []
    for (let y=0; y<graphData.data.length; y++) {
        let row = [];
        for (let x=0; x<graphData.data[y].length; x++) {
            row.push(
                {
                    x: graphData.xvalues[x],
                    y: graphData.yvalues[y],
                    z: graphData.data[y][x]
                }
            );
        }
        data.push(row);
    }
    return data;
}


export function getDefaultGraphData() {
    return transformGraphData(initializeInput());
}

export function CustomDataInput({settings, setSettings}) {
    const [graphData, setGraphData] = useState(initializeInput);

    function parseGraphData() {
        /*
        let data = []
        for (let y=0; y<graphData.data.length; y++) {
            let row = [];
            for (let x=0; x<graphData.data[y].length; x++) {
                row.push(
                    {
                        x: graphData.xvalues[x],
                        y: graphData.yvalues[y],
                        z: graphData.data[y][x]
                    }
                );
            }
            data.push(row);
        }
        */
        let data = transformGraphData(graphData);
        let settingsTemp = {...settings};
        settingsTemp.data = data;
        setSettings(settingsTemp);

        //console.log(graphData);
    }

    let rows = [];

    for (let i=0; i<graphData.data.length; i++) {
        rows.push(<InputRow key={i} graphData={graphData} setGraphData={setGraphData} yindex={i} />);
    }

    return (
        <div className="boxcontent">
            <p>
                Add data in table below. Add and remove rows or columns as needed.
            </p>
            <p>Click "set as graph data" to store changed data. Then click "(re-)render graph" at the bottom of the screen to display the graph.</p>
            <div className="custominputdata">
                <XLabelRow graphData={graphData} setGraphData={setGraphData} />
                {rows}
                <XButtonRow graphData={graphData} setGraphData={setGraphData} />
                <p>
                    <input type="button" value="Set as graph data" onClick={parseGraphData} />
                </p>
            </div>
        </div>
    );
}

function XLabelRow({graphData, setGraphData}) {

    let cells = [];

    for (let i=0; i<graphData["xvalues"].length; i++) {
        cells.push(
            <span className="inputcell" key={i}>
                <LabelCell graphData={graphData} setGraphData={setGraphData} labelname="xvalues" index={i} />
            </span>);
    }

    return(
        <p>
            <span className="ylabelheader"><span style={{color: 'rgb(255, 137, 77)'}}>Y values:</span> | <span style={{color: 'rgb(53, 170, 243)'}}>X values:</span> </span>
            <span className="xdatalabel">
                {cells}
            </span>
        </p>
    );
}

function XButtonRow({graphData, setGraphData}) {

    function remove(e) {
        let dataTemp = {...graphData};

        delete dataTemp.xvalues[e.target.id];
        dataTemp.xvalues = dataTemp.xvalues.filter(function (e) {return e;});

        for (let i=0; i<dataTemp.data.length; i++) {
            delete dataTemp.data[i][e.target.id];
            dataTemp.data[i] = dataTemp.data[i].filter(function (e) {return e;});            
        }

        setGraphData(dataTemp);
    }

    function insert(e) {
        let xindex = e.target.id;
        let dataTemp = {...graphData};
        dataTemp.xvalues.splice(xindex, 0, dataTemp.xvalues[xindex]);
        for (let i=0; i<dataTemp.data.length; i++) {
            dataTemp.data[i].splice(xindex, 0, dataTemp.data[i][e.target.id] ); 
        }
        setGraphData(dataTemp);
    }

    let cells = [];

    for (let i=0; i<graphData["xvalues"].length; i++) {
        cells.push(<span className="inputcell" key={i}>
            <input type="button" value="remove" id={i} onClick={remove} />
            <input type="button" value="insert" id={i} onClick={insert} />
            </span>);
    }

    return(
        <p>
            <span className="ylabelheader"></span>
            <span className="inputcellgroup">
                {cells}
            </span>
        </p>
    );

}

function InputRow({graphData, setGraphData, yindex}) {

    function remove(e) {
        let dataTemp = {...graphData};
        delete dataTemp.data[yindex];
        delete dataTemp.yvalues[yindex];
        dataTemp.data = dataTemp.data.filter(function (e) {return e;});
        dataTemp.yvalues = dataTemp.yvalues.filter(function (e) {return e;});
        setGraphData(dataTemp);
    }

    function insert(e) {
        let dataTemp = {...graphData};
        dataTemp.data.splice(yindex, 0, JSON.parse(JSON.stringify( dataTemp.data[yindex])));
        dataTemp.yvalues.splice(yindex, 0, dataTemp.yvalues[yindex]);
        setGraphData(dataTemp);
    }

    let cells = [];

    for (let i=0; i<graphData.data[yindex].length; i++) {
        cells.push(<InputCell key={i} setGraphData={setGraphData} graphData={graphData} yindex={yindex} xindex={i} />);
    }
    
    return(
        <p>
            <span className="ydatalabel ylabelheader"><LabelCell graphData={graphData} setGraphData={setGraphData} labelname="yvalues" index={yindex} /></span>
            <span className="inputcellgroup">{cells}</span>
            <input type="button" value="remove" onClick={remove} />
            <input type="button" value="insert before" onClick={insert} />
        </p>
    );
}

function InputCell({graphData, setGraphData, yindex, xindex}) {

    function updateValue(e) {
        let dataTemp = {...graphData};
        dataTemp.data[yindex][xindex] = e.target.value;
        setGraphData(dataTemp);
    }

    return(
        <span className="inputcell">
            <input type="number" size="4" value={graphData.data[yindex][xindex]} onChange={updateValue} />
        </span>
    );
}

function LabelCell({graphData, setGraphData, labelname, index}) {


    function updateValue(e) {
        let dataTemp = {...graphData};
        dataTemp[labelname][index] = e.target.value;
        setGraphData(dataTemp);
    }

    return(
        <>
            <input type="number" size="4" value={graphData[labelname][index]} onChange={updateValue} />
        </>
    );
}