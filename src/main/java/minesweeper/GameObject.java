package minesweeper;

//Для их описания игровых объектов (ячеек)
public class GameObject {
    public int x;
    public int y;
    //Является ли миной
    public boolean isMine;
    //Количество "заминированных" соседей для ячеек не мин
    public int countMineNeighbors;
    public boolean isOpen ;
    public boolean isFlag ;


    public GameObject(int x, int y, boolean isMine) {
        this.x = x;
        this.y = y;
        this.isMine = isMine;
        isFlag=false;
    }
}
