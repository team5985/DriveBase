package frc.robot.motorchecking;

public class motorCheckConstants {

    //Global
    public static final int kControllerBusVoltageShutdown = 2;
    public static final int kControllerBusVoltageWarning = 4;
    public static final int kMotorTemperatureShutdown = 80;
    public static final int kMotorTemperatureWarning = 55;
    public static final  double kPDPOutputCurrentShutdown = 0.8;
    public static final int kPDPOutputCurrentWarning = 2;
    public static final double kMotorOutputCurrentShutdown = 0.5;
    public static final int kMotorOutputCurrentWarning = 2;
    public static final int kEncoderVelocityShutdown = 3;
    public static final int kEncoderVelocityWarning = 9;
    public static final double kMinimumSpeedForCheck = 0.5;
    public static final int faultScanCountMinimum = 40;
}