package com.ardublock.translator.block.makeblock;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class MeJoystick extends TranslatorBlock {

	public MeJoystick(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label) {
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException {
		translator.addHeaderFile("Makeblock.h");
		translator.addHeaderFile("SoftwareSerial.h");
		translator.addHeaderFile("Wire.h");
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		String ret = "MeJoystick joystick"+translatorBlock.toCode()+"(PORT_"+translatorBlock.toCode()+");";
		translator.addDefinitionCommand(ret);

		TranslatorBlock axis = this.getRequiredTranslatorBlockAtSocket(1);
		return codePrefix + "joystick"+translatorBlock.toCode()+(axis.toCode().indexOf("x")>-1?".readX()":".readY()")+codeSuffix;
	}

}
