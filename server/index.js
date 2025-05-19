const express = require('express');
const WebSocket = require('ws');
const app = express();
const http = require('http').createServer(app);
const PORT = process.env.PORT || 5000;

// 创建WebSocket服务器
const wss = new WebSocket.Server({ server: http });

// 游戏状态
const gameState = {
  snakes: {},
  food: { x: 15, y: 10 },
  scores: {}
};

// 随机生成食物位置
function generateFood() {
  const x = Math.floor(Math.random() * (600 / 20));
  const y = Math.floor(Math.random() * (400 / 20));
  return { x, y };
}

// 广播游戏状态给所有客户端
function broadcastGameState() {
  wss.clients.forEach(client => {
    if (client.readyState === WebSocket.OPEN) {
      client.send(JSON.stringify({
        type: 'game-update',
        data: gameState
      }));
    }
  });
}

// WebSocket连接处理
wss.on('connection', (ws) => {
  const playerId = Date.now().toString();
  
  // 初始化玩家蛇
  gameState.snakes[playerId] = [{ x: 10, y: 10 }];
  gameState.scores[playerId] = 0;
  
  // 发送玩家ID给客户端
  ws.send(JSON.stringify({
    type: 'player-id',
    data: playerId
  }));
  
  // 广播新玩家加入
  broadcastGameState();
  
  // 处理客户端消息
  ws.on('message', (message) => {
    try {
      const data = JSON.parse(message);
      
      if (data.type === 'direction') {
        const { direction } = data;
        const snake = gameState.snakes[playerId];
        
        if (snake) {
          const head = { ...snake[0] };
          
          // 防止反向移动
          const isMovingBackwards = (
            (direction === 'UP' && snake.length > 1 && head.y - 1 === snake[1].y) ||
            (direction === 'DOWN' && snake.length > 1 && head.y + 1 === snake[1].y) ||
            (direction === 'LEFT' && snake.length > 1 && head.x - 1 === snake[1].x) ||
            (direction === 'RIGHT' && snake.length > 1 && head.x + 1 === snake[1].x)
          );
          
          if (isMovingBackwards) return;
          
          // 更新蛇头位置
          switch (direction) {
            case 'UP': head.y -= 1; break;
            case 'DOWN': head.y += 1; break;
            case 'LEFT': head.x -= 1; break;
            case 'RIGHT': head.x += 1; break;
          }
          
          // 移动蛇
          snake.unshift(head); // 添加新头部
          
          // 检查是否吃到食物
          if (head.x === gameState.food.x && head.y === gameState.food.y) {
            gameState.scores[playerId] += 10;
            gameState.food = generateFood();
          } else {
            snake.pop(); // 移除尾部
          }
          
          // 边界检查（穿墙）
          if (head.x < 0) head.x = 29;
          if (head.x > 29) head.x = 0;
          if (head.y < 0) head.y = 19;
          if (head.y > 19) head.y = 0;
          
          // 广播游戏更新
          broadcastGameState();
        }
      }
    } catch (error) {
      console.error('Error parsing message:', error);
    }
  });
  
  // 处理断开连接
  ws.on('close', () => {
    // 移除玩家
    delete gameState.snakes[playerId];
    delete gameState.scores[playerId];
    
    // 广播游戏更新
    broadcastGameState();
  });
});

// 配置静态文件服务 - 关键修改点
app.use(express.static('.')); // 提供当前目录下的所有静态文件

// 根路径处理
app.get('/', (req, res) => {
  res.sendFile(__dirname + '/index.html'); // 确保index.html在正确的路径下
});

// 启动服务器
http.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
