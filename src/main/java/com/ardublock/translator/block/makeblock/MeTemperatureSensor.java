package com.ardublock.translator.block.makeblock;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class MeTemperatureSensor extends TranslatorBlock {

	public MeTemperatureSensor(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label) {
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException {
		translator.addHeaderFile("Makeblock.h");
		translator.addHeaderFile("SoftwareSerial.h");
		translator.addHeaderFile("Wire.h");
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);

		TranslatorBlock slotBlock = this.getRequiredTranslatorBlockAtSocket(1);
		String slot = slotBlock.toCode();
		String ret = "MeTemperature temperature"+translatorBlock.toCode()+"(PORT_"+translatorBlock.toCode()+","+slot+");";
		translator.addDefinitionCommand(ret);
		return codePrefix + "temperature"+translatorBlock.toCode()+".temperature()"+codeSuffix;
	}

}
