const express = require('express');
const WebSocket = require('ws');
const path = require('path');
const cors = require('cors');

// 动态获取端口（适配 Codespaces 环境变量 PORT）
const port = process.env.PORT || 8080;
const app = express();

// 配置跨域：允许 Codespaces 域名和本地开发环境
const allowedOrigins = [];

// 动态添加 Codespaces 域名
if (process.env.GITHUB_CODESPACES_PORT_FORWARDING_DOMAIN) {
  const codespacesDomain = process.env.GITHUB_CODESPACES_PORT_FORWARDING_DOMAIN;
  allowedOrigins.push(`https://*.${codespacesDomain}`);
}

// 允许本地开发环境
allowedOrigins.push('http://localhost:*');

// 配置 CORS 中间件
app.use(cors({
  origin: (origin, callback) => {
    // 允许无来源的请求（如 Postman 或浏览器直接访问）
    if (!origin) return callback(null, true);
    
    // 检查来源是否匹配允许的域名
    const isAllowed = allowedOrigins.some(pattern => {
      // 将通配符模式转换为正则表达式
      const regex = new RegExp(`^${pattern.replace(/\*/g, '.*')}$`);
      return regex.test(origin);
    });
    
    if (isAllowed) {
      console.log(`✅ 允许跨域请求: ${origin}`);
      callback(null, true);
    } else {
      console.log(`❌ 拒绝跨域请求: ${origin}`);
      callback(new Error('跨域请求被拒绝'), false);
    }
  },
  methods: ['GET', 'POST', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization'],
  credentials: true,
  exposedHeaders: ['X-Request-ID']
}));

// 托管 public 目录作为静态资源根目录
app.use(express.static(path.join(__dirname, 'public')));

// 创建 HTTP 服务器并绑定到 0.0.0.0（允许公网访问）
const server = app.listen(port, '0.0.0.0', () => {
  // 输出公网访问链接（通过环境变量获取）
  const codespacesDomain = process.env.GITHUB_CODESPACES_PORT_FORWARDING_DOMAIN;
  if (codespacesDomain) {
    console.log(`✅ 服务器已启动（公网可访问）：https://${codespacesDomain}`);
  } else {
    console.log(`ℹ️ 本地开发模式：http://localhost:${port}`);
  }
});

// WebSocket 服务器初始化
const wss = new WebSocket.Server({ server });

// 存储客户端连接和游戏状态
const clients = new Set();
const gameState = {
  players: {},
  food: {
    x: Math.floor(Math.random() * 20) * 20,
    y: Math.floor(Math.random() * 20) * 20
  },
  score: {},
  nextPlayerId: 1
};

// 广播消息给所有客户端
function broadcast(data) {
  const json = JSON.stringify(data);
  clients.forEach(client => {
    if (client.readyState === WebSocket.OPEN) {
      client.send(json);
    }
  });
}

// 检测与其他蛇的碰撞
function checkCollision(playerId, headX, headY) {
  for (const id in gameState.players) {
    if (id === playerId) continue;
    const player = gameState.players[id];
    for (let i = 1; i < player.snake.length; i++) {
      if (player.snake[i].x === headX && player.snake[i].y === headY) {
        return true;
      }
    }
  }
  return false;
}

// 检测自身碰撞
function checkSelfCollision(playerId) {
  const player = gameState.players[playerId];
  const head = player.snake[0];
  for (let i = 1; i < player.snake.length; i++) {
    if (player.snake[i].x === head.x && player.snake[i].y === head.y) {
      return true;
    }
  }
  return false;
}

// 检测食物是否生成在蛇身上
function isFoodOnSnake(food) {
  for (const player of Object.values(gameState.players)) {
    if (player.snake.some(segment => segment.x === food.x && segment.y === food.y)) {
      return true;
    }
  }
  return false;
}

// 更新游戏状态
function updateGameState() {
  const playersToRemove = [];

  for (const playerId in gameState.players) {
    const player = gameState.players[playerId];
    let { x: headX, y: headY } = player.snake[0];
    const direction = player.direction;

    // 计算新蛇头位置
    switch (direction) {
      case 'LEFT': headX -= 20; break;
      case 'UP': headY -= 20; break;
      case 'RIGHT': headX += 20; break;
      case 'DOWN': headY += 20; break;
    }

    // 穿墙处理（边界循环）
    headX = headX < 0 ? 380 : headX >= 400 ? 0 : headX;
    headY = headY < 0 ? 380 : headY >= 400 ? 0 : headY;

    // 检测碰撞（与其他蛇或自身）
    const hasCollision = checkCollision(playerId, headX, headY) || checkSelfCollision(playerId);
    if (hasCollision) {
      playersToRemove.push(playerId);
      continue;
    }

    // 处理食物吞噬
    const ateFood = (headX === gameState.food.x && headY === gameState.food.y);
    if (ateFood) {
      gameState.score[playerId] = (gameState.score[playerId] || 0) + 1;
      // 重新生成食物直到不在蛇身上
      do {
        gameState.food = {
          x: Math.floor(Math.random() * 20) * 20,
          y: Math.floor(Math.random() * 20) * 20
        };
      } while (isFoodOnSnake(gameState.food));
    }

    // 更新蛇身（头部添加，尾部移除或保留）
    player.snake.unshift({ x: headX, y: headY });
    if (!ateFood) player.snake.pop();
  }

  // 移除碰撞的玩家
  playersToRemove.forEach(id => {
    broadcast({ type: 'playerCollision', data: id });
    delete gameState.players[id];
    delete gameState.score[id];
  });

  // 广播更新后的游戏状态（转换为数组避免遍历顺序问题）
  broadcast({
    type: 'gameState',
    data: {
      players: Object.values(gameState.players).map(player => ({
        id: player.id,
        snake: player.snake,
        direction: player.direction,
        color: player.color
      })),
      food: gameState.food,
      score: gameState.score
    }
  });
}

// 处理客户端连接
wss.on('connection', (ws) => {
  clients.add(ws);
  const playerId = gameState.nextPlayerId++;

  // 初始化玩家（随机颜色）
  gameState.players[playerId] = {
    id: playerId,
    snake: [{ x: 9 * 20, y: 10 * 20 }], // 初始位置
    direction: 'RIGHT', // 初始方向
    color: `hsl(${Math.random() * 360}, 70%, 60%)` // 随机颜色
  };

  // 发送玩家 ID 和初始游戏状态
  ws.send(JSON.stringify({ type: 'playerId', data: playerId }));
  ws.send(JSON.stringify({
    type: 'gameState',
    data: {
      players: Object.values(gameState.players),
      food: gameState.food,
      score: gameState.score
    }
  }));

  // 处理客户端发送的方向变更消息
  ws.on('message', (data) => {
    try {
      const msg = JSON.parse(data.toString());
      if (msg.type === 'changeDirection') {
        const player = gameState.players[playerId];
        const { direction } = msg;
        // 防止反向移动（如左 ↔ 右、上 ↔ 下）
        if (
          (direction === 'LEFT' && player.direction !== 'RIGHT') ||
          (direction === 'UP' && player.direction !== 'DOWN') ||
          (direction === 'RIGHT' && player.direction !== 'LEFT') ||
          (direction === 'DOWN' && player.direction !== 'UP')
        ) {
          player.direction = direction;
        }
      }
    } catch (error) {
      console.error('消息解析错误:', error);
    }
  });

  // 处理连接断开
  ws.on('close', () => {
    clients.delete(ws);
    delete gameState.players[playerId];
    delete gameState.score[playerId];
    broadcast({ type: 'playerLeft', data: playerId });
  });
});

// 启动游戏循环（每秒更新 10 次）
setInterval(updateGameState, 100);

// 错误处理中间件
app.use((err, req, res, next) => {
  console.error('服务器错误:', err);
  res.status(500).send('服务器内部错误');
});

console.log('✅ CORS 配置:', allowedOrigins);
