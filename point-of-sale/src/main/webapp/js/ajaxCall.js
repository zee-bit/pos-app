function makeAjaxCall(url, type, data, success, error = handleAjaxError, extraArgs = {}){
    if(type === 'GET')
    {
        $.ajax({
            url: url,
            type: type,
            ...extraArgs,
            headers: {
                'Content-Type': 'application/json'
            },
            success: success,
            error: error
        });
    }
    else{
       $.ajax({
            url: url,
            type: type,
            data: data,
            ...extraArgs,
            headers: {
                'Content-Type': 'application/json'
            },
            success: success,
            error: error
       });
    }
}