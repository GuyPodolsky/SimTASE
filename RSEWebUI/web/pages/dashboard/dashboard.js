function loadXMLFile() {
    let input = document.createElement('input');
    input.type = 'file';
    input.onchange = _ => {
        // you can use this method to get file and perform respective operations
        let files =   Array.from(input.files);
        console.log(files);
        // todo: add here the call to the loading xml servlet
    };
    input.click();

}

function stockSelected(r,rowId){
/*var symbol = document.getElementById(rowId).getElementsByTagName(dt)[0].value();
$(function ())*/

    let symbol = this.getElementsByTagName('dt')[0].value(); // todo: check if this works properly
    $.ajax({
        type:'get',
        data:symbol,
        url: "enter here",                                  // todo: replace the url to the real servlet
        success: function(result){
            window.location.reload(result);
        }});

}