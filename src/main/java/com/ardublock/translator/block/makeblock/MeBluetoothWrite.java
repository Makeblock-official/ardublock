package com.ardublock.translator.block.makeblock;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class MeBluetoothWrite extends TranslatorBlock {

	public MeBluetoothWrite(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label) {
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException {
		translator.addHeaderFile("Makeblock.h");
		translator.addHeaderFile("SoftwareSerial.h");
		translator.addHeaderFile("Wire.h");
		TranslatorBlock block = this.getRequiredTranslatorBlockAtSocket(0);
		String port = block.toCode();
		String ret = "MeBluetooth bluetooth"+port+"(PORT_"+port+");";
		translator.addDefinitionCommand(ret);
		block = this.getRequiredTranslatorBlockAtSocket(1);
		translator.addSetupCommand("bluetooth"+port+".begin("+block.toCode()+");");
		TranslatorBlock dataBlock = this.getRequiredTranslatorBlockAtSocket(2);
		return codePrefix+"bluetooth"+port+".print("+dataBlock.toCode()+");\n"+codeSuffix;
	}

}
