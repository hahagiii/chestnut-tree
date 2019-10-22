package io.chestnut.core.service.serviceMrg;

import io.chestnut.core.ChestnutTree;
import io.chestnut.core.ChestnutTreeOption;
import io.chestnut.core.orm.EntityMrg;

public class ServiceMrg {
	public static EntityMrg entityMrg;
	public static ChestnutTree chestnutTree;

	public static void main(String[] args) throws Exception {
		entityMrg = new EntityMrg("127.0.0.1", 27017, "root", "", "serviceMrg");
		ChestnutTreeOption chestnutTreeOption = new ChestnutTreeOption();
		chestnutTreeOption.httpdOpt(8012,"io.chestnut.core.service.serviceMrg.httpHandle");
		chestnutTree = new ChestnutTree(chestnutTreeOption);
		chestnutTree.run();
	}
}
