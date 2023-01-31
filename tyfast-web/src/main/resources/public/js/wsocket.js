/**
 * 建立WebSocket STOMP连接
 */
let stompClient;
function connect() {
  let sock = new SockJS(ctx + "stomp");
  stompClient = webstomp.over(sock);
  stompClient.hasDebug = false;
  stompClient.connect({}, function() {
    app.socketState = 9;
    app.onConnected && app.onConnected();
    console.log("STOMP服务器连接成功");

    // 订阅点对点消息：接收账户下线通知
    stompClient.subscribe("/user/queue/kickout", function(message) {
      if (message.body && !app.kickout) {
        stompClient.disconnect(() => {
          console.log("STOMP服务正常断开")
        });
        app.socketState = 1;
        app.kickout = true;
        let msgObj = JSON.parse(message.body);
        alert(msgObj.data);
        app.logout();
      }
    });
  }, function() {
    9 === app.socketState && app.onDisconnected && app.onDisconnected();
    app.socketState = 1;
    console.log("STOMP服务器连接异常");

    // 5秒后自动重连
    setTimeout(() => {
      connect();
    }, 5000)
  });
  return stompClient;
}
