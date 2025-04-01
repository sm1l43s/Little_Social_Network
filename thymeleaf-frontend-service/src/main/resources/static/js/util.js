    function strToObject(str) {
    let jsonStr = str.replace(/(\w+:)|(\w+ :)/g, function(matchedStr) {
        return '"' + matchedStr.substring(0, matchedStr.length - 1) + '":';
    });
    return JSON.parse(jsonStr);
    }