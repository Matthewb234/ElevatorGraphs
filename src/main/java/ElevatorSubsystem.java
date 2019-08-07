
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.team2073.common.position.converter.PositionConverter;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;


public class ElevatorSubsystem extends Subsystem {
    public TalonSRX elevatorMaster = new TalonSRX(5);
    public VictorSPX elevatorSlave = new VictorSPX(5);
    private Solenoid bikeBrakeSolenoid = new Solenoid(2);
    private boolean elevatorActive;
    private boolean bikeBrakeEngaged;
    private boolean bikeBrakeDisengaged;
    private Joystick controller;
    public boolean heightReached;
    private boolean brakeApplied;
    public double velocity;
    public BikeBrake bikeBrake = new BikeBrake();
    private static final double elevatorPercent = 1;
    private static final double MAX_BREAK_HEIGHT = 20;
    private static final int ENCODER_EDGES_PER_INCH_OF_TRAVEL = 1350;
    private static final int BIKE_BRAKE_RELEASE_DELAY = 60;

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

    private void setMotor(){
        System.out.println(currentPosition);
        if(currentPosition() < MAX_BREAK_HEIGHT && bikeBrake.isBikeBrakeDisengaged() && heightReached == false){
            setOutput(elevatorPercent);
        }
        if(currentPosition() > MAX_BREAK_HEIGHT){
             setOutput(0d);
             heightReached = true;
        }
    }

    public double getVelocity(){
        return velocity;
    }

    private void updateVelocity(){
        velocity = elevatorMaster.getSelectedSensorVelocity()/1190.15*10;
    }

//|| elevatorMaster.getSelectedSensorVelocity() < -.1
    private void applyBrake(){
        if(currentPosition() >= MAX_BREAK_HEIGHT && brakeApplied == false ){
            bikeBrake.engageBikeBrake();
            brakeApplied = true;
        }
    }

    public void init() {
        setSlave();
        bikeBrake.disengageBikeBrake();
        configEncoder();
    }

    public void configEncoder() {
        elevatorMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 10);
        elevatorMaster.configAllowableClosedloopError(0, 0, 10);
        elevatorMaster.setInverted(false);
        elevatorSlave.setInverted(true);

    }
    public static double getElevatorPercent(){
        return elevatorPercent;
    }

    @Override
    public void periodic() {

        updateCurrentPosition();
        updateVelocity();
        bikeBrake.periodic();
        if(elevatorActive) {
            setMotor();
            applyBrake();
            System.out.println("Output is " + elevatorMaster.getMotorOutputPercent());

        }
        if(bikeBrakeEngaged){
            bikeBrake.engageBikeBrake();
        }
        if(bikeBrakeDisengaged){
            bikeBrake.disengageBikeBrake();
        }
    }

    @Override
    protected void initDefaultCommand() {

    }



    public void setElevatorActive(boolean elevatorActive) {
        this.elevatorActive = elevatorActive;
    }

    public void setBikeBrakeEngaged(boolean bikeBrakeEngaged){
        this.bikeBrakeEngaged = bikeBrakeEngaged;
    }

    public void setBikeBrakeDisengaged(boolean bikeBrakeDisengaged){
        this.bikeBrakeDisengaged = bikeBrakeDisengaged;
    }


    public double currentPosition() {
        return currentPosition;
    }

    private void updateCurrentPosition() {
        currentPosition = elevatorMaster.getSelectedSensorPosition()/1190.15;
    }


    public enum BikeBrakeState {
        ENGAGED, DISENGAGING, ENGAGING, DISENGAGED
    }


    public class BikeBrake {

        /**
         * Renamed from bikeBreak
         */
        private BikeBrakeState bikeBrakeState;
        private long bikeDisengageTimeStamp;
        private long bikeEngageTimeStamp;

        public void periodic() {
//            updateState();
        }

        /**
         * Renamed from brakeElevator
         */
        public void engageBikeBrake() {
            bikeBrakeSolenoid.set(false);
            bikeBrakeState = BikeBrakeState.ENGAGED;
        }

        /**
         * Renamed from releaseBrake
         */
        public void disengageBikeBrake() {
            System.out.println("disengaged");
            bikeBrakeSolenoid.set(true);
            bikeBrakeState = BikeBrakeState.DISENGAGED;
        }


        private void updateState() {
            if (isBikeBrakeDisengaging()) {
                long timeSinceBrakeDisengaged = timeSinceBrakeDisengaged();
                if (timeSinceBrakeDisengaged > ElevatorSubsystem.BIKE_BRAKE_RELEASE_DELAY) {
//                    System.out.println("Bike delay reached. Safe to assume bike brake disengaged. Total wait time: [" + timeSinceBrakeDisengaged + "]");
                    bikeBrakeState = BikeBrakeState.DISENGAGED;
                }
            } else if (isBikeBrakeEngaging()) {
                long timeSinceBrakeEngaged = timeSinceBrakeEngaged();
                if (timeSinceBrakeEngaged > ElevatorSubsystem.BIKE_BRAKE_RELEASE_DELAY) {
//                    System.out.println("Bike delay reached. Safe to assume bike brake engaged. Total wait time: [" + timeSinceBrakeEngaged+"]");
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

