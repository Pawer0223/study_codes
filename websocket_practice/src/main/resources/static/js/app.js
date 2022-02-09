let stompClient = null;

// 로그인 시스템 대신 임시 방편
let username = prompt("아이디를 입력하세요");
// let roomNum = prompt("채팅방 번호를 입력하세요");

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
    const socket = new SockJS('/socket-end-point');
    stompClient = Stomp.over(socket);
    // sockJs 및 stomp.js를 사용해 /gs-guide-websocket 요청을 통해 연결을 open 한다.
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        // "/topic/greeting" 에 대한 요청을 구독하게 된다.
        stompClient.subscribe('/subscribe/greetings', function (greeting) {
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
        stompClient.send("/publish/hello", {}, JSON.stringify({'name': $("#name").val()}))
    );
    $("#name").val("");
}

// 1. 로그인이 되어있다고 가정.

// 2. 매칭하기 클릭 시, 새로운 방이 만들어 지고

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
