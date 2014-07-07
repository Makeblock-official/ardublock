package com.ardublock.translator.block.makeblock;

import org.jfree.util.Log;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class MePort extends TranslatorBlock {

	public MePort(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label) {
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException {
	
		String port_name = this.getTranslator().getBlock(blockId).getGenusName();
		String port_str = port_name.split("_")[2];
		return port_str;
	
	}

}
