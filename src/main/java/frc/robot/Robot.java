// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SlewRateLimiter;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpiutil.math.MathUtil;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  public static boolean isJoystick = false;

  public static boolean isSyzygy = true; 


  WPI_TalonSRX left;
  WPI_TalonSRX right; 
  WPI_VictorSPX leftF; 
  WPI_VictorSPX rightF; 

  WPI_TalonFX[] chassis;

  DifferentialDrive drive;
  Joystick joy;
  XboxController controller;
  PowerDistributionPanel pdp = new PowerDistributionPanel();

  VictorSPX[] intake = new VictorSPX[3];
  SlewRateLimiter yFilter = new SlewRateLimiter(5);
  SlewRateLimiter zFilter = new SlewRateLimiter(5);
 

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    intake[0] = new VictorSPX(7);
    intake[1] = new VictorSPX(1);
    intake[2] = new VictorSPX(2);
    for(int i = 1; i < 3; i++){
      intake[i].setInverted(true);
      intake[i].configPeakOutputForward(0.6);
      intake[i].configPeakOutputReverse(-0.6);
    }
    intake[0].configPeakOutputForward(0.9);
    intake[0].configPeakOutputReverse(-0.9);
    if(isSyzygy){
      chassis = new WPI_TalonFX[4];
      for(int i = 18; i < 22; i++){
        chassis[i - 18] = new WPI_TalonFX(i);
        chassis[i - 18].configFactoryDefault();
        chassis[i - 18].setInverted(true);
        // chassis[i - 18].setNeutralMode(NeutralMode.Brake);
      }
      chassis[0].follow(chassis[1]);
      chassis[2].follow(chassis[3]);
      drive = new DifferentialDrive(chassis[1], chassis[3]);

    }else{
      left = new WPI_TalonSRX(0);
      right = new WPI_TalonSRX(9);
      leftF = new WPI_VictorSPX(2);
      rightF = new WPI_VictorSPX(3);
      left.configFactoryDefault();
      leftF.configFactoryDefault();
      right.configFactoryDefault();
      rightF.configFactoryDefault();
      // left.setNeutralMode(NeutralMode.Brake);
      // right.setNeutralMode(NeutralMode.Brake);
      rightF.follow(right);
      leftF.follow(left);
      right.setInverted(true);
      rightF.setInverted(true);
      left.setInverted(true);
      leftF.setInverted(true);
      drive = new DifferentialDrive(left, right);
    }
    // leftF.setVoltage(10);
    // left.setVoltage(10);

    if (isJoystick) {
      joy = new Joystick(0);
    } else {
      controller = new XboxController(1);
    }
  }

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString line to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure below with additional strings. If using the SendableChooser
   * make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
   
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    // UsbCamera camera = 
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    if(isJoystick){
      drive.arcadeDrive(yFilter.calculate(joy.getY())*-0.8, zFilter.calculate(joy.getZ())*-0.6);
      if(joy.getRawButton(7) && pdp.getCurrent(9) < 35){
        intake[0].set(ControlMode.PercentOutput, -0.9);
        intake[1].set(ControlMode.PercentOutput, 0.5);
        intake[2].set(ControlMode.PercentOutput, 0.6);
      }else if(joy.getRawButton(2) && pdp.getCurrent(9) < 35){
        intake[0].set(ControlMode.PercentOutput, 0.9);
        intake[1].set(ControlMode.PercentOutput, -0.5);
        intake[2].set(ControlMode.PercentOutput, -0.6);
      }else{
        intake[0].set(ControlMode.PercentOutput, 0);
        intake[1].set(ControlMode.PercentOutput, 0);
        intake[2].set(ControlMode.PercentOutput, 0);
      }
      // drive.arcadeDrive(joy.getY() * 0.7, joy.getZ()* -0.5);
      // drive.curvatureDrive(joy.getY()*0.6, joy.getZ()*0.5, joy.getTrigger());
    }else{
      double x = zFilter.calculate(MathUtil.clamp(controller.getRawAxis(2), -1, 1)) * 0.55;
      double y = yFilter.calculate(MathUtil.clamp(controller.getRawAxis(1), -1, 1)) * -0.75;
      
      drive.tankDrive(y - x, y + x);

      if(controller.getRawButton(5) && pdp.getCurrent(9) < 35){
        intake[0].set(ControlMode.PercentOutput, -0.9);
        intake[1].set(ControlMode.PercentOutput, 0.5);
        intake[2].set(ControlMode.PercentOutput, 0.6);      
      }else if(controller.getRawButton(6)  && pdp.getCurrent(9) < 35){
        intake[0].set(ControlMode.PercentOutput, 0.9);
        intake[1].set(ControlMode.PercentOutput, -0.5);
        intake[2].set(ControlMode.PercentOutput, -0.6);   
      }else{
        intake[0].set(ControlMode.PercentOutput, 0);
        intake[1].set(ControlMode.PercentOutput, 0);
        intake[2].set(ControlMode.PercentOutput, 0);
      }
    }
  }


  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
