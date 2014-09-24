package com.ardublock.translator.block.makeblock;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class MeRGBLed extends TranslatorBlock {

	public MeRGBLed(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label) {
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException {
		translator.addHeaderFile("Makeblock.h");
		translator.addHeaderFile("SoftwareSerial.h");
		translator.addHeaderFile("Wire.h");
		TranslatorBlock block = this.getRequiredTranslatorBlockAtSocket(0);
		String led = "led"+block.toCode();
		
		String port = block.toCode();

		block = this.getRequiredTranslatorBlockAtSocket(1);
		String indexLed = block.toCode();

		block = this.getRequiredTranslatorBlockAtSocket(2);
		String r = block.toCode();

		block = this.getRequiredTranslatorBlockAtSocket(3);
		String g = block.toCode();

		block = this.getRequiredTranslatorBlockAtSocket(4);
		String b = block.toCode();
		
		String ret = "MeRGBLed "+led+"(PORT_"+port+");";
		translator.addDefinitionCommand(ret);
		translator.addSetupCommand(led+".clear();\n");
		
		return codePrefix+led+".setColorAt("+indexLed+","+r+","+g+","+b+");\n"+led+".show();"+codeSuffix;
	}

}