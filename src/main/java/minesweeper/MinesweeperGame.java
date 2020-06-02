package minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField = 0;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    //Количество неиспользованных флагов
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        restart();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) == 5;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellValue(x, y, "");
                setCellColor(x, y, Color.LIGHTGRAY);
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped) {
            restart();
        } else {
            openTile(x, y);
        }
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }

    //Найти всех соседей для ячейки
    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    // Подсчет сколько соседних ячеек "заминировано"
    private void countMineNeighbors() {
        List<GameObject> countMineNeighbors;
        for (int x = 0; x < gameField.length; x++) {
            for (int y = 0; y < gameField[x].length; y++) {
                countMineNeighbors = getNeighbors(gameField[y][x]);
                if (!gameField[y][x].isMine) {
                    for (GameObject object : countMineNeighbors) {
                        if (object.isMine) {
                            gameField[y][x].countMineNeighbors++;
                        }
                    }
                }
            }
        }
    }

    //Метод отвечающий за открытие ячеек. При открытии ячейки:рисовать в ячейке мину, если там мина;
// отображать количество мин-соседей, если в ячейке нет мины;отмечать ячейку модели открытой;менять цвет ячейки.
    private void openTile(int x, int y) {
        if (!isGameStopped) {
            if (!gameField[y][x].isFlag) {
                if (!gameField[y][x].isOpen) {
                    gameField[y][x].isOpen = true;
                    setCellColor(x, y, Color.DARKGRAY);
                    if (gameField[y][x].isMine) {
                        setCellValue(x, y, MINE);
                        setCellValueEx(x, y, Color.RED, MINE);
                        gameOver();
                    } else {
                        score = score + 5;
                        if (gameField[y][x].countMineNeighbors == 0) {
                            List<GameObject> list = getNeighbors(gameField[y][x]);
                            for (GameObject object : list) {
                                if (!object.isOpen) {
                                    openTile(object.x, object.y);
                                }
                            }
                            setCellValue(x, y, "");
                        } else {
                            setCellNumber(x, y, gameField[y][x].countMineNeighbors);
                        }
                    }
                    countClosedTiles--;
                }
            }
        }
        if (!gameField[y][x].isMine) {
            if (countClosedTiles == countMinesOnField) {
                win();
            }
        }
        setScore(score);
    }

    //Маркировка ячейки флажком. Отмечать ячейку на игровом поле флагом или снимать флаг,
//менять цвет ячейки поля, если в ней устанавливается флаг и возвращать цвет обратно,если флаг снимается.
    private void markTile(int x, int y) {
        if (!isGameStopped) {
            if (!gameField[y][x].isOpen) {
                if (countFlags != 0) {
                    if (!gameField[y][x].isFlag) {
                        gameField[y][x].isFlag = true;
                        countFlags--;
                        setCellValue(x, y, FLAG);
                        setCellColor(x, y, Color.YELLOW);
                    } else {
                        gameField[y][x].isFlag = false;
                        countFlags++;
                        setCellValue(x, y, "");
                        setCellColor(x, y, Color.LIGHTGRAY);
                    }
                }
            }
        }
    }

    //Будет останавливать игру и сообщать игроку о проигрыше.
    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.RED, "GAME OVER", Color.BLACK, 50);
    }

    //Игра считается выигранной, когда количество оставшихся закрытых ячеек равно количеству мин.
    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.LIGHTGOLDENRODYELLOW, "YOU WIN", Color.BLACK, 50);
    }

    //Запускать игру, сбрасывать в исходное состояние, количество закрытых ячеек, мин на поле и очков
    private void restart() {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        countMinesOnField = 0;
        setScore(score);
        createGame();
    }
}