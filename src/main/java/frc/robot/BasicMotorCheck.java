package frc.robot;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMax;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Spark;

public class BasicMotorCheck {
    PowerDistributionPanel m_PDP;
    int m_controllerChannel;
    Spark m_spark;
    int scancount;

    public BasicMotorCheck(Spark spark, int controllerChannel, PowerDistributionPanel pdp) {
        m_spark = spark;
        m_controllerChannel = controllerChannel;
        m_PDP = pdp;
        scancount = 0;
    }
    public void update() {
        BasicMotorCheck();
        if(Math.abs(m_spark.get()) >= kMinimumSpeedForCheck){
        scancount++;
        }
    }   

    /*
     * Comms with m_spark max CANSPARKMax.class line 819???? Errors reported by
     * m_spark max CANSPARKMax.class Line 90 Bus Voltage as detected by sparkmax
     * CANSPARKMax.class line 705 Motor current as detected by m_spark max
     * CANSPARKMax.class line 719 Current as detected by m_PDP Motor Rotations on
     * Encoder CANSPARKMax.class Line 257 Motor Tempertaure CANSPARKMax.class Line
     * 726
     * 
     * CHECK IF ENCODER CONNECTED;;; //FIXME 
     */
    public int BasicMotorCheck(){
    if(Math.abs(m_spark.get()) >= kMinimumSpeedForCheck){
        if(scancount >= 15){
            if(checkPDPCurrent() == 2)
            {
            return 2;
            }
            else if(checkPDPCurrent() == 1)
            {
            return 1;
            }
            else
            {
            return 0;
            }
        }
        else{
        return 0;
        }
    }
    else{
        return 0;
    }
    }

        
   
   
 
   
    private int checkPDPCurrent() {
        if (m_PDP.getCurrent(m_controllerChannel) < kPDPOutputCurrentShutdown) {
            return 2;
        } else if (m_PDP.getCurrent(m_controllerChannel) < kPDPOutputCurrentWarning
                && m_PDP.getCurrent(m_controllerChannel) >= kPDPOutputCurrentShutdown) {
            return 1;
        } else {
            return 0;
        }
    }



    private final int kPDPOutputCurrentShutdown = 1;
    private final int kPDPOutputCurrentWarning = 2;
    private final double kMinimumSpeedForCheck = 0.4;

}


/*  
  private int[] errorArray = {};
public int[] sparkMaxMotorCheck(){ 
 errorArray[0] = checkControllerComms(m_spark); 
 errorArray[1] = checkControllerError(m_spark);
 errorArray[2] = checkControllerBusVoltage(m_spark); 
 errorArray[3] = checkMotorCurrent(m_spark); 
 errorArray[4] = checkPDPCurrent(m_PDP, m_controllerChannel); 
 errorArray[5] = checkEncoderRotations(m_spark, m_encoder);
 errorArray[6] = checkMotorTemperature(m_spark); return errorArray;
 }
 */