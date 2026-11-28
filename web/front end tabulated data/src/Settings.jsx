import {useState} from 'react';

export function Settings({settings, setSettings}) {


    return (
        <div className="box">
            <h2>Settings</h2>
            <p>
                <Label text="Graph image size (pixels)" />
                <TextInput label="Image width" id="width" settings={settings} setSettings={setSettings} />|
                <TextInput label="Image height" id="height" settings={settings} setSettings={setSettings} />
            </p>
            <p>
                <Label text="X-axis" />
                <TextInput label="min X-axis value" id="minX" settings={settings} setSettings={setSettings} />|
                <TextInput label="max X-axis value" id="maxX" settings={settings} setSettings={setSettings} />| 
                <TextInput label="X-axis label" id="xlabel" settings={settings} setSettings={setSettings} />
            </p>
            <p>
                <Label text="Y-axis" />
                <TextInput label="min Y-axis value" id="minY" settings={settings} setSettings={setSettings} />|
                <TextInput label="max Y-axis value" id="maxY" settings={settings} setSettings={setSettings} />|
                <TextInput label="Y-axis label" id="ylabel" settings={settings} setSettings={setSettings} />                
            </p>
            <p>
                <Label text="Z-axis" />
                <TextInput label="min Z-axis value" id="minZ" settings={settings} setSettings={setSettings} />|
                <TextInput label="max Z-axis value" id="maxZ" settings={settings} setSettings={setSettings} />|
                <TextInput label="Z-axis label" id="zlabel" settings={settings} setSettings={setSettings} />
            </p>
            <p>                
                <Label text="" />
                <CheckBoxInput label="Auto adjust Z-axis min and max values to data" id="autoAdjustZ" settings={settings} setSettings={setSettings} />
            </p>
            <p>
                <Label text="Intersection of axes at" />
                <TextInput label="X-axis" id="xOffset" settings={settings} setSettings={setSettings} />|
                <TextInput label="Y-axis" id="yOffset" settings={settings} setSettings={setSettings} />|
                <TextInput label="Z-axis" id="zOffset" settings={settings} setSettings={setSettings} />                
            </p>  
            <p>
                <Label text="Mesh size" />
                <SliderInput label="Mesh size" id="steps" min={10} max={100} settings={settings} setSettings={setSettings} />  
                <span>Larger mesh size means more datapoints.</span>
            </p>
            <p>
                <Label text="Scaling factor for Z-axis" />
                <SliderInput label="Scaling factor" id="scalingFactorZ" min={0.1} max={10} settings={settings} setSettings={setSettings} />  
                <span>Smaller scaling factor will flatten graph.</span>
            </p>
            <p>
                <Label text="Display settings" />
                <CheckBoxInput label="Show axes" id="showAxis" settings={settings} setSettings={setSettings} />|
                <CheckBoxInput label="Show grid" id="showGrid" settings={settings} setSettings={setSettings} />|
                <CheckBoxInput label="Show labels" id="showLabels" settings={settings} setSettings={setSettings} />|
                <SliderInput label="Transparency" id="transparency" min={0} max={255} settings={settings} setSettings={setSettings} />                           
            </p>                      
        </div>
    );
}



function Label({text}) {
    return (
        <span className="label">{text}: </span>
    );
}

function TextInput({label, id, settings, setSettings}) {

    function updateSettings(e) {
        let settingsTemp = {...settings};
        settingsTemp[id] = e.target.value;
        setSettings(settingsTemp);
    }

    return (
        <label>
            <span className="settingsLabel">{label}: </span>
            <input type="text" size="6" name={id} value={settings[id]} onChange={updateSettings} />
        </label>
    );
}

function CheckBoxInput({label, id, settings, setSettings}) {
    function updateSettings(e) {
        let settingsTemp = {...settings};
        settingsTemp[id] = e.target.checked;
        setSettings(settingsTemp);
    }

    return (
        <label>
            <input type="checkbox" name={id} checked={settings[id]} onChange={updateSettings} />
            <span className="settingsLabel">{label} </span>
        </label>
    );
}

function SliderInput({label, id, settings, setSettings, min, max}) {
    function updateSettings(e) {
        let settingsTemp = {...settings};
        settingsTemp[id] = e.target.value;
        setSettings(settingsTemp);
    }

    return (
        <label>
            <span className="settingsLabel">{label}: </span>
            <input type="range" name={id} min={min} max={max} value={settings[id]} onChange={updateSettings} />
        </label>
    );
}