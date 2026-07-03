let apiserver = "http://localhost:8080/image";
//let apiserver = "https://apps.guidobreuer.nl/api/image";

function copytoclipboard() {
    let text = document.getElementById("label");
    text.select();
    text.setSelectionRange(0,9999);
    navigator.clipboard.writeText(text.value);
}

document.form.requestSubmit();


function handleSubmit() {
    let url = new URL(apiserver);


    url.searchParams.append('equation', document.form.equation.value);
    url.searchParams.append('rotation', document.form.rotation.value);    
    url.searchParams.append('vrot', document.form.vrot.value);
    url.searchParams.append('zoom', document.form.zoom.value);        
    url.searchParams.append('width', document.form.width.value);                                      
    url.searchParams.append('height', document.form.height.value); 
    url.searchParams.append('minx', document.form.minx.value); 
    url.searchParams.append('miny', document.form.miny.value); 
    url.searchParams.append('minz', document.form.minz.value); 
    url.searchParams.append('maxx', document.form.maxx.value); 
    url.searchParams.append('maxy', document.form.maxy.value); 
    url.searchParams.append('maxz', document.form.maxz.value);     
    if (document.getElementById("autoadjustz").checked) {
        url.searchParams.append('autoadjustz', 'true');
    } else {
        url.searchParams.append('autoadjustz', 'false');
    }
    url.searchParams.append('xoffset', document.form.xoffset.value);    
    url.searchParams.append('yoffset', document.form.yoffset.value);    
    url.searchParams.append('zoffset', document.form.zoffset.value);                            
    url.searchParams.append('steps', document.form.steps.value);    
    url.searchParams.append('alpha', document.form.alpha.value);    

    let factor = Math.pow(10, document.form.scalingfactorz.value);
    url.searchParams.append('scalingfactorz', factor); 

    if (document.getElementById("showaxis").checked) {
        url.searchParams.append('showaxis', 'true');
    } else {
        url.searchParams.append('showaxis', 'false');
    }   
    if (document.getElementById("showgrid").checked) {
        url.searchParams.append('showgrid', 'true');
    } else {
        url.searchParams.append('showgrid', 'false');
    }
    if (document.getElementById("showlabel").checked) {
        url.searchParams.append('showlabel', 'true');
    } else {
        url.searchParams.append('showlabel', 'false');
    }                        
    document.form.label.value = url;
    document.getElementById("image").src = url;
}




function rotate(amount) {
    document.form.rotation.value = parseFloat(document.form.rotation.value) + amount;
    document.form.requestSubmit();

}

function vrotate(amount) {
    let phi = parseFloat(document.form.vrot.value) + amount;
    if (phi < 0) phi = 0.00;
    if (phi > 1) phi = 1;
    document.form.vrot.value = phi;
    document.form.requestSubmit();
}