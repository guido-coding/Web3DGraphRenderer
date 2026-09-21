import {useState} from 'react';

export function Settings({settings, setSettings}) {


    return (
        <div className="box">
            <h2>Settings</h2>
            <p>
                <Label text="Graph image size" />
                <TextInput label="width" id="width" settings={settings} setSettings={setSettings} />|
                <TextInput label="height" id="height" settings={settings} setSettings={setSettings} />
            </p>
            <p>
                <Label text="X-axis" />
                <TextInput label="min X" id="minX" settings={settings} setSettings={setSettings} />|
                <TextInput label="max X" id="maxX" settings={settings} setSettings={setSettings} />
            </p>
            <p>
                <Label text="Y-axis" />
                <TextInput label="min Y" id="minY" settings={settings} setSettings={setSettings} />|
                <TextInput label="max Y" id="maxY" settings={settings} setSettings={setSettings} />
            </p>
            <p>
                <Label text="Z-axis" />
                <TextInput label="min Z" id="minZ" settings={settings} setSettings={setSettings} />|
                <TextInput label="max Z" id="maxZ" settings={settings} setSettings={setSettings} />|
                <CheckBoxInput label="Auto adjust" id="autoAdjustZ" settings={settings} setSettings={setSettings} />
            </p>
            <p>
                <Label text="Offset of axes" />
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
                <SliderInput label="Scaling factor" id="steps" min={0.1} max={10} settings={settings} setSettings={setSettings} />  
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