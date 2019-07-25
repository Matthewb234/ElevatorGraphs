
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.team2073.common.position.converter.PositionConverter;
import com.team2073.common.speedcontroller.EagleSPX;
import com.team2073.common.speedcontroller.EagleSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;


public class ElevatorSubsystem extends Subsystem {

    private EagleSRX elevatorMaster;
    private EagleSPX elevatorSlave;
    private Solenoid bikeBrakeSolenoid;
    private Joystick controller;
    private boolean elevatorActive;
    private BikeBrake bikeBrake;


    public static final int ENCODER_EDGES_PER_INCH_OF_TRAVEL = 1350;

    private static final int BIKE_BRAKE_RELEASE_DELAY = 60;

    private static final int MOTOR_KILL_HEIGHT = 23;

    private PositionConverter converter = new PositionConverterImpl();

    private double currentPosition;

    public ElevatorSubsystem(Joystick controller) {
        this.controller = controller;
    }

    public void setOutput(double output) {
        elevatorMaster.set(ControlMode.PercentOutput, output);
    }

    public void setSlave() {

        elevatorSlave.follow(elevatorMaster);
    }

    private void killMotor(){
        if(currentPosition() < MOTOR_KILL_HEIGHT){
            setOutput(0.1);
        }else if(currentPosition() >= MOTOR_KILL_HEIGHT){
            setOutput(0d);
        }
    }

    private void applyBrake(){
        if(currentPosition() >= 25 || elevatorMaster.getSelectedSensorVelocity() <= 0){
            bikeBrake.engageBikeBrake();
        }
    }
    public void init() {
        configEncoder();
        setSlave();
    }

    public void configEncoder() {
        elevatorMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 10);
        elevatorMaster.configAllowableClosedloopError(0, 0, 10);
//        elevatorMaster.setInverted(Elevator.MASTER_DEFAULT_DIRECTION);
//        elevatorSlave.setInverted(Elevator.SLAVE_DEFAULT_DIRECTION);

    }

    @Override
    public void periodic() {
        updateCurrentPosition();
        bikeBrake.periodic();
        if(elevatorActive) {
            killMotor();
            applyBrake();
        }
    }

    @Override
    protected void initDefaultCommand() {

    }



    public void setElevatorActive(boolean elevatorActive) {
        this.elevatorActive = elevatorActive;
    }

    private double currentPosition() {
        return currentPosition;
    }

    private void updateCurrentPosition() {
        currentPosition = converter.asPosition(elevatorMaster.getSelectedSensorPosition(0));
    }


    public enum BikeBrakeState {
        ENGAGED, DISENGAGING, ENGAGING, DISENGAGED;
    }


    private class BikeBrake {

        /**
         * Renamed from bikeBreak
         */
        private BikeBrakeState bikeBrakeState;
        private long bikeDisengageTimeStamp;
        private long bikeEngageTimeStamp;

        public void periodic() {
            updateState();
        }

        /**
         * Renamed from brakeElevator
         */
        public void engageBikeBrake() {
            if (isBikeBrakeDisengaged()) {
                bikeDisengageTimeStamp = -1;
                bikeEngageTimeStamp = System.currentTimeMillis();
                System.out.println("Engaging bike brake. Position: [{}]" + currentPosition);
                bikeBrakeSolenoid.set(false);
                bikeBrakeState = BikeBrakeState.ENGAGING;
            }
        }

        /**
         * Renamed from releaseBrake
         */
        public void disengageBikeBrake() {
            if (isBikeBrakeEngaged()) {
                bikeEngageTimeStamp = -1;
                bikeDisengageTimeStamp = System.currentTimeMillis();
                System.out.println("Disengaging bike brake. Position: [{}]" + currentPosition);
            }
            bikeBrakeSolenoid.set(true);
            bikeBrakeState = BikeBrakeState.DISENGAGING;
        }


        private void updateState() {
            if (isBikeBrakeDisengaging()) {
                long timeSinceBrakeDisengaged = timeSinceBrakeDisengaged();
                if (timeSinceBrakeDisengaged > ElevatorSubsystem.BIKE_BRAKE_RELEASE_DELAY) {
                    System.out.println("Bike delay reached. Safe to assume bike brake disengaged. Total wait time: [{}]" + timeSinceBrakeDisengaged);
                    bikeBrakeState = BikeBrakeState.DISENGAGED;
                }
            } else if (isBikeBrakeEngaging()) {
                long timeSinceBrakeEngaged = timeSinceBrakeEngaged();
                if (timeSinceBrakeEngaged > ElevatorSubsystem.BIKE_BRAKE_RELEASE_DELAY) {
                    System.out.println("Bike delay reached. Safe to assume bike brake engaged. Total wait time: [{}]" + timeSinceBrakeEngaged);
                    bikeBrakeState = BikeBrakeState.ENGAGED;
                }
            }
        }

        public boolean isBikeBrakeEngaged() {
            return bikeBrakeState == BikeBrakeState.ENGAGED;
        }

        public boolean isBikeBrakeDisengaged() {
            return bikeBrakeState == BikeBrakeState.DISENGAGED;
        }


        public boolean isBikeBrakeEngaging() {
            return bikeBrakeState == BikeBrakeState.ENGAGING;
        }

        public boolean isBikeBrakeDisengaging() {
            return bikeBrakeState == BikeBrakeState.DISENGAGING;
        }

        public long timeSinceBrakeDisengaged() {
            return System.currentTimeMillis() - bikeDisengageTimeStamp;
        }

        public long timeSinceBrakeEngaged() {
            return System.currentTimeMillis() - bikeEngageTimeStamp;
        }
    }

    private class PositionConverterImpl implements PositionConverter {

        @Override
        public double asPosition(int tics) {
            return (double) (tics / ENCODER_EDGES_PER_INCH_OF_TRAVEL);
        }

        @Override
        public int asTics(double position) {
            return (int) (position * ENCODER_EDGES_PER_INCH_OF_TRAVEL);
        }

        @Override
        public String positionalUnit() {
            return Units.INCHES;
        }

    }
}

