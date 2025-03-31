window.addEventListener("load", () => {
    connect();
});

var stompClient = null;
var selectedUser = null;
var userName = $("#from").val();
var newMessage = [];

function setConnected(connected) {
    $("#from").prop("disabled", connected);
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);

    if (connected) {
        $("#users").show();
        $("#sendmessage").show();
    } else {
        $("#users").hide();
        $("#sendmessage").hide();
    }
}

function connect() {
    userName = $("#from").val();
    if (userName == null || userName === "") {return; }

    $.post('/rest/user-connect',
        { username: userName },
        function(remoteAddr, status, xhr) {
            var socket = new SockJS('/chat');
            stompClient = Stomp.over(socket);
            stompClient.connect({username: userName}, function () {
                stompClient.subscribe('/topic/broadcast', function (output) {
                    showMessage(createTextNode(JSON.parse(output.body)));
                });

                stompClient.subscribe('/topic/active', function () {
                    updateUsers(userName);
                });

                stompClient.subscribe('/user/queue/messages', function (output) {
                    let data = JSON.parse(output.body);
                    if (selectedUser === data.from || selectedUser === data.recipient) {
                        showMessage(createTextNode(JSON.parse(output.body)));
                    } else {
                        newMessage.push(data);
                        let users = document.querySelectorAll("li");
                        for (let  i = 0; i < users.length; i++) {
                            if (users[i].textContent == data.from) {
                                let childs = users[i].childNodes;
                                childs[0].innerHTML = childs[0].innerHTML.split(" ")[0] + " " + childs[0].innerHTML.split(" ")[1] +
                                    "<span class='m-1 p-1 pr-2 pl-2 rounded-circle bg-danger text-white'>+1</span>";
                            }
                        }
                    }
                });

                sendConnection(' connected to server');
                setConnected(true);
            }, function (err) {
                //alert('error' + err);
            });

        }).done(function() {
        // alert('Request done!');
    }).fail(function(jqxhr, settings, ex) {
            console.log('failed, ' + ex);
        }
    );
}

function disconnect() {
    if (stompClient != null) {
        $.post('/rest/user-disconnect',
            { username: userName },
            function() {
                sendConnection(' disconnected from server');
                stompClient.disconnect(function() {
                    console.log('disconnected...');
                    setConnected(false);
                });

            }).done(function() {
            // alert('Request done!');
        }).fail(function(jqxhr, settings, ex) {
                console.log('failed, ' + ex);
            }
        );
    }
}

function sendConnection(message) {
    var text = userName + message;
    sendBroadcast({'from': 'server', 'text': text});
    // for first time or last time, list active users:
    updateUsers(userName);
}

function sendBroadcast(json) {
    stompClient.send("/app/broadcast", {}, JSON.stringify(json));
}

function send() {
    var text = $("#message").val();
    if (selectedUser == null) {
        alert('Выберите пользователя для беседы.');
        return;
    }

    stompClient.send("/app/chat", {'sender': userName},
        JSON.stringify({'from': userName, 'text': text, 'recipient': selectedUser}));
    $("#message").val("");
}

function createTextNode(messageObj) {
    var justify = "justify-content-end";
    var classAlert = 'msg_container';
    var fromTo = messageObj.from;
    var addTo =  fromTo;

    if (userName == messageObj.from) {
        fromTo = messageObj.recipient;
        addTo =  'to: ' + fromTo;
    }

    if (userName != messageObj.from && messageObj.from != "server") {
        classAlert = "msg_container_send";
        justify = "justify-content-start";
    }

    if (messageObj.from != "server") {
        addTo = '<a href="javascript:void(0)" onclick="setSelectedUser(\'' + fromTo + '\')">' + addTo + '</a>'
    }

    return '<div class="d-flex ' + justify + ' p-2 mb-4">' +
        '<div class="'+ classAlert +'">' + messageObj.text +
        '<div class="text-danger"><small><i class="far fa-clock"></i>' + messageObj.time + '</small></div>\n' +
        '</div></div>';




}

function showMessage(message) {
    let content = document.getElementById("content");
    content.innerHTML += message;
}

function clearMessages() {
    $("#content").html("");
    $("#clear").hide();
}

function setSelectedUser(elem) {
    elem.innerHTML = elem.innerHTML.split(" ")[0] + " " + elem.innerHTML.split(" ")[1];
    selectedUser = elem.innerHTML.split(" ")[0] + " " + elem.innerHTML.split(" ")[1];

    $("#content").html("");
    $("#selectedUser").html("Беседа с " + selectedUser);
    if ($("#selectedUser").html() == "") {
        $("#divSelectedUser").hide();
    }
    else {
        $("#divSelectedUser").show();
    }
    let allMsg = [];
    $.ajax({
        type: 'POST',
        url: '/privateMessage',
        data: {username: selectedUser},
        success: function(response) {
            allMsg = createTrueMsgObject(strToObject(response));
            if (newMessage.length != 0) {
                for (let i = 0; i < allMsg.length; i++) {
                    for (let j = 0; j < newMessage.length; j++) {
                        if (newMessage[j].from == allMsg[i].from && newMessage[j].recipient == allMsg[i].recipient
                            && newMessage[j].text == allMsg[i].text && newMessage[j].time == allMsg[i].time) {
                            allMsg.splice(i, 1);
                        }
                    }
                }
            }

            for (let i = 0; i < allMsg.length; i++) {
                for (let j = 1; j < allMsg.length - 1; j++) {
                    let a = allMsg[i];
                    let b = allMsg[j];
                    allMsg.sort(function(a,b){
                        return new Date("2020-11-20 " + a.time) - new Date("2020-11-20 " + b.time);
                    });
                }
            }

            for (let i = 0; i < allMsg.length; i++) {
                showMessage(createTextNode(allMsg[i]));
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("error occurred");
        }
    });

    for (let i = 0; i < newMessage.length; i++) {
        if (newMessage[i].from === selectedUser) {
            showMessage(createTextNode(newMessage[i]));
        }
    }
}

function updateUsers(userName) {

    // console.log('List of users : ' + userList);
    var activeUserSpan = $("#active-users-span");
    var activeUserUL = $("#active-users");

    var index;
    activeUserUL.html('');

    var url = '/rest/active-users-except/' + userName;
    $.ajax({
        type: 'GET',
        url: url,
        // data: data,
        dataType: 'json', // ** ensure you add this line **
        success: function(userList) {

            if (userList.length == 0) {
                activeUserSpan.html('<p><i>Нет пользователей онлайн.</i></p>');
            } else {
                activeUserSpan.html('<p class="text-muted">Выберите пользователя для того чтобы начать общение</p>');
                for (index = 0; index < userList.length; ++index) {
                    if (userList[index] != userName) {
                        activeUserUL.html(activeUserUL.html() + createUserNode(userList[index], index));
                    }
                }
            }
        },

        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("error occurred");
        }
    });
}

function createUserNode(username, index) {
    return '<li class="">' +
            '<a class="active-user" onclick="setSelectedUser(this)">' + username + '</a>' +
            '</li>';
}

function createTrueMsgObject(arrMsg) {
    let msg = [];
    for (let i = 0; i < arrMsg.length; i++) {
        let obj = {
            from: arrMsg[i].from,
            recipient: arrMsg[i].recipient,
            text: arrMsg[i].message,
            time: arrMsg[i].hours + ":" + arrMsg[i].minutes + ":" + arrMsg[i].seconds
        };
        msg.push(obj);
    }
    return msg;
}



