let stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    const socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    // sockJs 및 stomp.js를 사용해 /gs-guide-websocket 요청을 통해 연결을 open 한다.
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        // "/topic/greeting" 에 대한 요청을 구독하게 된다.
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

// 화면에 입력한 이름을 STOMP 클라이언트를 사용하여 /app/hello 보냄(GreetingController.greeting())
async function sendName() {
    await(
        stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}))
    );
    document.querySelector("#name").value = "";
}

// AJAX 채팅 메시지를 전송
async function addMessage() {
    let msgInput = document.querySelector("#chat-outgoing-msg");

    let chat = {
        sender: username,
        roomNum: roomNum,
        msg: msgInput.value
    };

    fetch("http://localhost:8080/chat", {
        method: "post", //http post 메서드 (새로운 데이터를 write)
        body: JSON.stringify(chat), // JS -> JSON
        headers: {
            "Content-Type": "application/json; charset=utf-8"
        }
    });

    msgInput.value = "";
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});