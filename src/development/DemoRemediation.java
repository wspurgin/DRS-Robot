/*
 * need to implement the following:
 * String moveSensor(int caseNum); returns String with sensor move location
 * int measureTurbidity(); //returns turbidity in NTU
 * double measurepH(int pH);
 * double measureTempInC();
 */
boolean testNRemediate()
{
    boolean fail = false;
    int mixerSpeed = 255;
    int numReRun = 3;
    //max speed is 255
    String challenge = getChallenge();
    int case1 = 1;
    int case2 = 2;
    int case3 = 3;
    if (challenge.equals("Below Ground"))
        moveSensor(case1);
    else if (challenge.equals("Above Ground"))
        moveSensor(case3);
    else if (challenge.equals("Ground Level"))
        moveSensor(case2);
    else
        return fail;
    //create Scanner object in case
    //need to type in given starting pH
    double temperature = measureTempInC();
    double doseNeeded = calculateDose(pH);
    double pH = measurepH();
    setMixerSpeed(mixerSpeed);//from RXTXRobot class
    //DETERMINE WHEN TO STOP MIXER
    stopMixer();
    double pHFinal = measurepH(); //should be in 7-7.5 range
    int pH[numReRuns];
    for (int rerun = 0; rerun < numReRuns; rerun++)
        pH[rerun] = measurepH();
}
double measurepH();
int measureTurbidity(); //returns turbidity in NTU
String moveSensor(int caseNum); //returns String with sensor move location
