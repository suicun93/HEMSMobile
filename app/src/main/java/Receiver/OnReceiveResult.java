package Receiver;/** * @author hoang-trung-duc */import com.sonycsl.echo.EchoProperty;public interface OnReceiveResult {      default void controlResult(boolean success) {            controlResult(success, null);      }      void controlResult(boolean success, EchoProperty echoProperty);}