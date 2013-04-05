//import rxtx.coord check exact path
double [] getCurrentPosition()
{
    //x is right-left out of 8
    //y is front-back out of 8
    double distance[4];
    int fDistance = distances[0] = getDistance();//from ping sensor
    turn();//actually use moveServo(int, int) in rxtxrobot
    double rDistance = distances[1] = getDistance();
    turn();
    double bDistance = distances[2] = getDistance();
    turn();
    double lDistance = distances[3] = getDistance();
    turn();//return to original position    
    return distances[];
}    
Coord moveDirection()
{
    Coord go = new Coord(0.0, 0.0, 0.0);
    double [] start = getCurrentPosition();
    go.setY(start[0] - start[2]);
    go.setX(start[1] - start[3]);
    int CRITICAL_DISTANCE = 10; //
    if (getDistance() <= 10)
    {
        turn();
    }
    //if(some critical time elapsed || some distance traveled)
    //getCurrentPosition();
}
