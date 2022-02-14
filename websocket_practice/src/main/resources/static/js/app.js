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
    const socket = new SockJS('/socket-end-point');
    stompClient = Stomp.over(socket);
    // sockJs 및 stomp.js를 사용해 /socket-end-point 요청을 통해 연결을 open 한다.
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

// // 화면에 입력한 이름을 STOMP 클라이언트를 사용하여 /publish/hello 보냄(GreetingController.greeting())
// async function sendName() {
//     await(
//         stompClient.send("/publish/hello", {}, JSON.stringify({'name': $("#name").val()}))
//     );
//     $("#name").val("");
// }

// 화면에 입력한 이름을 STOMP 클라이언트를 사용하여 /publish/hello 보냄(GreetingController.greeting())
function sendName() {
    stompClient.send("/publish/hello", {}, JSON.stringify({'name': $("#name").val()}))
    $("#name").val("");
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
