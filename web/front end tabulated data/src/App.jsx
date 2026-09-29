import { useState } from 'react'
import {Settings} from './Settings.jsx';
import {initialSettings} from './InitialSettings.jsx';
import {initialInput} from './InitialSettings.jsx';
import {CustomDataInput} from './DataInput.jsx';
import './App.css'

const apiurl = document.getElementById("apiurl").value;

function App() {
  const [errorMessage, setErrorMessage] = useState(null);
  const [settings, setSettings] = useState(initialSettings);  
  const [input, setInput] = useState(initialInput);
  const [inputmode, setInputmode] = useState("table");

  function submit() {
    postRequest(settings, setErrorMessage);    
  }

  return (
    <>
      <h1>Graph3D renderer</h1>
      <p>Use user-entered data to render 3D graphs.</p>
      <ErrorMessage errorMessage={errorMessage} setErrorMessage={setErrorMessage} />    
      <Submit submit={submit} />
      <div>
        <SelectInputMode inputmode={inputmode} setInputmode={setInputmode} />

        {
          (inputmode == "text") ? 
          <DataInput settings={settings} setSettings={setSettings} input={input} setInput={setInput} />
          : <></>  
        }
        {
          (inputmode == "table") ? 
          <CustomDataInput settings={settings} setSettings={setSettings} />
          : <></> 
        }      
      </div>

      <GraphViewer settings={settings} setSettings={setSettings} />
      <Settings settings={settings} setSettings={setSettings} />
      <div className="bottom"></div>
    </>
  )
}

function SelectInputMode({inputmode, setInputmode}) {

  function update(e) {
    e.preventDefault();
    setInputmode(e.target.id);
    document.getElementById(e.target.id).className = "selected";
  }

  return (
    <div className="boxheader">
      <p className="buttons">
        <span>Select Input mode: </span>
        {
          (inputmode=="text") ?
          <>
          <a href="#" id="text" onClick={update} style={{backgroundColor: "rgb(105, 105, 202)"}}>As delimited text</a> 
          <a href="#" id="table" onClick={update} >As tabulated data</a>        
          </>
          :
          <>
          <a href="#" id="text" onClick={update} >As delimited text</a> 
          <a href="#" id="table" onClick={update} style={{backgroundColor: "rgb(105, 105, 202)"}} >As tabulated data</a>
          </> 
        }
      </p>
    </div>
  );
}

function DataInput({settings, setSettings, input, setInput}) {
  
  function handleChange(e) {
    setInput(e.target.value);
  }

  function update() {
    let settingsTemp = {...settings};
    settingsTemp.data = parseData(input);
    setSettings(settingsTemp);
  }
  
  return(
    <div className="boxcontent">
      <p>Add data in rows. Separate each value in a row using a semicolon (';'). 
        First row is reserved for x-axis values. First column is reserve for y-axis values. 
        All other values list the z values for the x and y coordinate from the column and row in which the data are added. </p>
        <p>Click "set as graph data" to store changed data. Then click "(re-)render graph" at the bottom of the screen to display the graph.</p>
      <p>
        <textarea rows="10" cols="100" id="data" value={input} onChange={handleChange} />
      </p>
      <p>
        <input type="button" value="Set as graph data" onClick={update} />
      </p>
    </div>
  );
}

function Submit({submit}) {
  
  
  return (
    <div className="submit">
      <input type="button" value="(Re-)render graph" onClick={submit} />
    </div>
  );
}

function ErrorMessage({errorMessage, setErrorMessage}) {
  
  function reset(e) {
    e.preventDefault();
    setErrorMessage(null);
  }
  
  return (
    <>
    {
      (errorMessage != null) ?
      <div className="error">
        <p>
          <span>
            <b><u>Error:</u></b> {errorMessage}
          </span>
          <span>
            |
          </span>
          <span>
            <a href="#" onClick={reset} >X</a>
          </span>
        </p>
      </div>
      :
      <></>
      
    }
    </>
  );
}

function GraphViewer({settings, setSettings}) {

  function updateSettings(e) {
        let settingsTemp = {...settings};
        settingsTemp[e.target.name] = e.target.value;
        setSettings(settingsTemp);
      }
      
      return (
        <div className="box">
        <h2>View graph</h2>
        <p className="imageWrapper">
          <span id="image"></span>
          <span id="vrot"><label><input type="range" orient="vertical" name="vrot" min="0" max="1" step="0.1" onChange={updateSettings} /><br />Vertical pan</label></span>
          <span id="rotation"><label>Rotation: <input type="range" name="rotation" min="-1" max="1" step="0.1" onChange={updateSettings} /></label></span>
          <span id="zoom"><label>View distance: <input type="range" name="zoom" min="50" max="200" step="10" onChange={updateSettings} /></label></span>
        </p>
      </div>
  );
}

function parseData(data) {
  let parsedData = [];
  data.trim()
    .split("\n")
    .forEach(
      (element) => parsedData.push(
        element.split(";")
        .map(
          (value) => parseFloat(value.trim())
        )
      )
    );

  let graphData = [];
  for (let y=1; y<parsedData.length; y++) {
    let row = [];
    for (let x=1; x<parsedData[y].length; x++) {
      row.push(
        {
          x: parsedData[0][x],
          y: parsedData[y][0],
          z: parsedData[y][x]
        }
      );
    }
    graphData.push(row);
  }

  return graphData;
}

function postRequest(settings, setErrorMessage) {
  
  fetch(apiurl + "customdataimage", {
    method: "POST",  
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(settings)
  })
  .then(response => {
    if (!response.ok) { 
      return response.text().then(err => Promise.reject(response.status + ": " + response.statusText + " | "  + err.substring(0,50)));
    } else {
      return response.text();
    }
  })
  .then(data => {
    document.getElementById('image').innerHTML = "<img src='" + apiurl + "retrieve/" + data + "' />";
    setErrorMessage(null);
  })
  .catch((error) => {;
    console.log(error);
    setErrorMessage(error.toString());
  });
}

export default App
