package com.ardublock.translator.block.makeblock;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Me7SegmentDisplay extends TranslatorBlock {

	public Me7SegmentDisplay(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label) {
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException {
		translator.addHeaderFile("Makeblock.h");
		translator.addHeaderFile("SoftwareSerial.h");
		translator.addHeaderFile("Wire.h");
		TranslatorBlock block = this.getRequiredTranslatorBlockAtSocket(0);
		String sevseg = "sevseg"+block.toCode();
		
		block = this.getRequiredTranslatorBlockAtSocket(1);
		String port = block.toCode();

		block = this.getRequiredTranslatorBlockAtSocket(2);
		String num = block.toCode();
		String ret = "Me7SegmentDisplay "+sevseg+"(PORT_"+port+");";
		translator.addDefinitionCommand(ret);
		return sevseg+".display("+num+");";
	}

}
