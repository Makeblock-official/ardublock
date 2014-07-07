package com.ardublock.translator.block.makeblock;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.NumberBlock;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.BlockException;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class MeServoDriver extends TranslatorBlock {

	public MeServoDriver(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label) {
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException {
		translator.addHeaderFile("Makeblock.h");
		translator.addHeaderFile("SoftwareSerial.h");
		translator.addHeaderFile("Wire.h");
		translator.addHeaderFile("Servo.h");
		TranslatorBlock block = this.getRequiredTranslatorBlockAtSocket(0);
		String port = block.toCode();
		String servo = "servoDriver"+port;
		block = this.getRequiredTranslatorBlockAtSocket(1);
		String device = block.toCode();
		
		String ret = "Servo "+servo+";";
		translator.addDefinitionCommand(ret);

		translator.addSetupCommand("int pinName = "+(device.equals("1")?"mePort["+port+"].s1;":"mePort["+port+"].s2;"));
		translator.addSetupCommand(servo+".attach(pinName);");
		String output = "";
		block = this.getRequiredTranslatorBlockAtSocket(2);
		if(block instanceof NumberBlock){
			int angle = Integer.parseInt(block.toCode());
			if(angle>180||angle<0){
				throw new BlockException(this.blockId, "the angle of Servo must be in Range(0,180)");
			}
			angle = angle>180?180:(angle<0?0:angle);
			output+= servo+".write("+angle+");\n";
		}else{
			output+= servo+".write("+block.toCode()+");\n";
		}
		return output;
	}

}
