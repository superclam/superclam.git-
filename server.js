const express = require('express');
const app = express();
const http = require('http').Server(app);
const io = require('socket.io')(http);
const PORT = process.env.PORT || 3000;

// 存储游戏房间信息，这里简单处理，只考虑一个房间
const room = {
    players: [],
    map: Array.from({ length: 15 }, () => Array(15).fill(0)),
    currentPlayer: 1
};

// 静态文件服务，用于提供前端页面
app.use(express.static(__dirname));

io.on('connection', (socket) => {
    console.log('A user connected');

    // 玩家加入房间
    socket.on('joinRoom', () => {
        if (room.players.length < 2) {
            room.players.push(socket.id);
            socket.join('gameRoom');
            socket.emit('joinedRoom', {
                playerNumber: room.players.length,
                map: room.map,
                currentPlayer: room.currentPlayer
            });

            if (room.players.length === 2) {
                io.to('gameRoom').emit('gameStart');
            }
        } else {
            socket.emit('roomFull');
        }
    });

    // 玩家落子
    socket.on('makeMove', (data) => {
        const { row, col } = data;
        if (room.map[row][col] === 0 && room.currentPlayer === (room.players.indexOf(socket.id) + 1)) {
            room.map[row][col] = room.currentPlayer;
            room.currentPlayer = room.currentPlayer === 1? 2 : 1;
            io.to('gameRoom').emit('updateBoard', {
                map: room.map,
                currentPlayer: room.currentPlayer
            });

            // 检查是否胜利
            if (checkWin(room.map, row, col, room.currentPlayer === 1? 2 : 1)) {
                io.to('gameRoom').emit('gameOver', room.currentPlayer === 1? 2 : 1);
            }
        }
    });

    // 玩家断开连接
    socket.on('disconnect', () => {
        const index = room.players.indexOf(socket.id);
        if (index!== -1) {
            room.players.splice(index, 1);
            io.to('gameRoom').emit('playerLeft');
        }
    });
});

// 检查胜利的函数
function checkWin(map, row, col, player) {
    const directions = [
        [0, 1], [1, 0], [1, 1], [1, -1]
    ];
    for (const [dx, dy] of directions) {
        let count = 1;
        for (let i = 1; i < 5; i++) {
            const newRow = row + i * dy;
            const newCol = col + i * dx;
            if (newRow >= 0 && newRow < 15 && newCol >= 0 && newCol < 15 && map[newRow][newCol] === player) {
                count++;
            } else {
                break;
            }
        }
        for (let i = 1; i < 5; i++) {
            const newRow = row - i * dy;
            const newCol = col - i * dx;
            if (newRow >= 0 && newRow < 15 && newCol >= 0 && newCol < 15 && map[newRow][newCol] === player) {
                count++;
            } else {
                break;
            }
        }
        if (count >= 5) {
            return true;
        }
    }
    return false;
}

http.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});
