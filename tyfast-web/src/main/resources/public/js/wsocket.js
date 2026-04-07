/**
 * 建立WebSocket STOMP连接
 */
let stompClient;
function connect() {
  let sock = new SockJS(ctx + "stomp");
  stompClient = webstomp.over(sock);
  stompClient.hasDebug = false;
  stompClient.connect({}, function() {
    appInstance.socketState = 9;
    appInstance.onConnected && appInstance.onConnected();
    console.log("STOMP服务器连接成功");

    // 订阅机构广播消息
    stompClient.subscribe("/topic/message/" + appInstance.tenantId, function(message) {
      handleBroadcastMessage(message.body);
    });
    subscribeWEvent();

    // 订阅点对点消息：接收账户下线通知
    stompClient.subscribe("/user/queue/kickout", function(message) {
      if (message.body && !appInstance.kickout) {
        stompClient.disconnect(() => {
          console.log("STOMP服务正常断开")
        });
        appInstance.socketState = 1;
        appInstance.kickout = true;
        let msgObj = JSON.parse(message.body);
        alert(msgObj.data);
        appInstance.logout();
      }
    });
  }, function() {
    9 === appInstance.socketState && appInstance.onDisconnected && appInstance.onDisconnected();
    appInstance.socketState = 1;
    console.log("STOMP服务器连接异常");

    // 5秒后自动重连
    setTimeout(() => {
      connect();
    }, 5000)
  });
  return stompClient;
}

/**
 * 订阅WebSocket业务事件
 */
function subscribeWEvent() {
  appInstance && appInstance.subscribeWEvent && appInstance.subscribeWEvent();
}

/**
 * 解析WebSocket消息对象
 */
function parseSocketMessage(message) {
  let socketMessage = null;
  if (typeof(message) === "string" && message.startsWith("{")) {
    socketMessage = JSON.parse(message);
  }
  return socketMessage;
}

/**
 * 处理广播消息
 */
function handleBroadcastMessage(message) {
  let socketMessage = parseSocketMessage(message);
  if (socketMessage) {
    switch (socketMessage.type) {
      case "alarm": // 处理告警消息
        let deviceAlarm = JSON.parse(socketMessage.data || "{}");
        appInstance.alarmMessages.unshift(deviceAlarm);

        // 在业务系统的浏览器选项卡被激活的状态下，才进行提示
        if (appInstance.tabActivity) {
          // Toast提示
          let level = deviceAlarm.alarmLevel || 1;
          let text = appInstance.alarmLevels[level] + "：" + (deviceAlarm.deviceAlias || deviceAlarm.deviceName) + deviceAlarm.alarmName;
          appInstance.toast(text, level > 1? 'warning' : 'info');

          // 语音提示
          if (!appInstance.alarmSilent) {
            playTTS(text);
          }
        }
        break;
      case "clearalarm": // 清除告警消息
        if ("all" === socketMessage.data) {
          appInstance.alarmMessages = [];
        } else {
          let deviceAlarm = JSON.parse(socketMessage.data || "{}");
          appInstance.alarmMessages = appInstance.alarmMessages.filter(item => {
            return item.alarmId !== deviceAlarm.alarmId;
          });
        }
        break;
    }
    // 回调业务的自定义逻辑处理
    appInstance && appInstance.handleBroadcastMessage && appInstance.handleBroadcastMessage(socketMessage);
  }
}