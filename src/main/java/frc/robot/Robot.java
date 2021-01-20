/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2020 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.auto.AutoMode;
import frc.robot.subsystems.Drive;
import edu.wpi.first.wpilibj.I2C;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  AutoController m_AutoController;

  Drive drive = Drive.getInstance();

  Spark LeftDrive = new Spark(7);
  Spark RightDrive = new Spark(8);

  Encoder LeftEnc = new Encoder(1, 2); 
  Encoder RightEnc = new Encoder(3, 4);
  
  Joystick joystick = new Joystick(0);
  double steerDirection = 0;
  double power = 0;
  double throttle = 0;

  UltrasonicI2C usi2c;
  boolean usTrigger = false;


  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    drive.setSystem(LeftDrive, RightDrive, RightEnc, LeftEnc);
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    I2C.Port i2cp = I2C.Port.kOnboard;
    I2C usLink = new I2C(i2cp, 0x13);
    usi2c = new UltrasonicI2C(usLink);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {

  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
    m_AutoController.getInstance().initialiseAuto();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    m_AutoController.getInstance().runAuto();
    }
  

  /**
   * This function is called once when teleop is enabled.
   */
  @Override
  public void teleopInit() {
  }


  double lastError = 0;
  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    
    
    if(joystick.getTriggerPressed()) {
      usTrigger = !usTrigger;
    }
    SmartDashboard.putBoolean("Trigger", usTrigger);
    usi2c.update();
    UltrasonicI2C.usResults results = usi2c.getResults();
    double res;
    if (results == null)
    {
      res = 0;
    }
    else
    {

      res = results.getResult();
    }
    SmartDashboard.putNumber("Distance", res);


    if (!usTrigger)
    {
      if (power >= -0.025 && power <= 0.025) {
        power = 0;
      }
      if (steerDirection >= -0.025 && steerDirection <= 0.025) {
        steerDirection = 0;
      }

      throttle = (-1 * joystick.getThrottle() + 1) / 2;
      steerDirection = joystick.getX() * throttle;
      power = -1 * joystick.getY() * throttle;
      LeftDrive.set(steerDirection - power);
      RightDrive.set(steerDirection + power);
    }
    else
    {

      double aimPos = 300;
      double gain = 0.0005;
      double dgain = 0.01;



      if (results.getNew())
      {
        double error = aimPos - results.getResult();
        double delta = error - lastError;
        lastError = error;
        steerDirection = (error * gain) + (delta * dgain);
        power = 1;
        LeftDrive.set(steerDirection - power);
        RightDrive.set(steerDirection + power);
      }
    }


  }

  /**
   * This function is called once when the robot is disabled.
   */
  @Override
  public void disabledInit() {
  }

  /**
   * This function is called periodically when disabled.
   */
  @Override
  public void disabledPeriodic() {
  }

  /**
   * This function is called once when test mode is enabled.
   */
  @Override
  public void testInit() {
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
