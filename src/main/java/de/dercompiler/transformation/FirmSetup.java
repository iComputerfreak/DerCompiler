package de.dercompiler.transformation;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Firm;

public class FirmSetup {

    public static void firmSetUp() {
        if (TargetTriple.isWindows()) {
            Firm.VERSION = Firm.FirmVersion.RELEASE;
        } else if (TargetTriple.isMacOS()) {
            Firm.VERSION = Firm.FirmVersion.FIRM;
        } else if (TargetTriple.isLinus()) {
            Firm.VERSION = Firm.FirmVersion.RELEASE;
        } else {
            new OutputMessageHandler(MessageOrigin.GENERAL).printInfo("We didn't recognize your operatingsystem, this may result in undefined behaviour.");
        }
    }

    public static void firmSetupDebug() {
        if (TargetTriple.isWindows()) {
            Firm.VERSION = Firm.FirmVersion.FIRM;
            System.setProperty("jna.library.path", "B:\\debug_libfirm\\Debug");
        } else if (TargetTriple.isMacOS()) {
            Firm.VERSION = Firm.FirmVersion.FIRM;
        } else if (TargetTriple.isLinus()) {
            Firm.VERSION = Firm.FirmVersion.RELEASE;
        } else {
            new OutputMessageHandler(MessageOrigin.GENERAL).printInfo("We didn't recognize your operatingsystem, this may result in undefined behaviour.");
        }
    }

    public static void firmSetupForTests() {
        if (TargetTriple.isWindows()) {
            Firm.VERSION = Firm.FirmVersion.RELEASE;
        } else if (TargetTriple.isMacOS()) {
            Firm.VERSION = Firm.FirmVersion.FIRM;
        } else if (TargetTriple.isLinus()) {
            Firm.VERSION = Firm.FirmVersion.RELEASE;
        } else {
            new OutputMessageHandler(MessageOrigin.GENERAL).printInfo("We didn't recognize your operatingsystem, this may result in undefined behaviour.");
        }
    }
}
