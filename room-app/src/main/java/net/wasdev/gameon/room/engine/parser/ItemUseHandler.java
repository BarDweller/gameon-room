package net.wasdev.gameon.room.engine.parser;

import java.util.ArrayList;

public abstract class ItemUseHandler extends CommandHandler {
	public static class CommandTemplateBuilder {
		private ArrayList<CommandTemplate.ParseNode> args = new ArrayList<CommandTemplate.ParseNode>();

		private void addArg(Node.Type type, String arg) {
			switch (type) {
			case VERB:
				throw new RuntimeException("Verb cannot be used with Item use handlers, as the verb is already set as 'use'");
			case LINKWORD:
				if (arg.contains(":") || arg.contains("/"))
					throw new RuntimeException("Verb and linkword cannot contain character : or /");
			default:
			}

			CommandTemplate.ParseNode p = new CommandTemplate.ParseNode();
			p.type = type;
			p.data = arg;
			args.add(p);
		}

		public ItemUseHandler.CommandTemplateBuilder build(Node.Type type, String arg) {
			switch (type) {
			case USER:
			case EXIT:
			case ROOM_ITEM:
			case INVENTORY_ITEM:
			case CONTAINER_ITEM:
			case ITEM_INSIDE_CONTAINER_ITEM:
				if (arg != null)
					throw new RuntimeException("No string  argument can be supplied for type " + type.name());
			default:
			}
			addArg(type, arg);
			return this;
		}

		public ItemUseHandler.CommandTemplateBuilder build(Node.Type type) {
			switch (type) {
			case VERB:
			case LINKWORD:
				throw new RuntimeException("Verb and linkword must be built with the string as an argument");
			default:
			}
			return build(type, null);
		}

		public CommandTemplate build() {

			StringBuilder sb = new StringBuilder();
			for (CommandTemplate.ParseNode n : args) {
				switch (n.type) {
				case USER:
					sb.append("/U:");
					break;
				case EXIT:
					sb.append("/E:");
					break;
				case ROOM_ITEM:
					sb.append("/R:");
					break;
				case INVENTORY_ITEM:
					sb.append("/I:");
					break;
				case CONTAINER_ITEM:
					sb.append("/C:");
					break;
				case ITEM_INSIDE_CONTAINER_ITEM:
					sb.append("/B:");
					break;
				case VERB:
					throw new RuntimeException("Verb node type unsupoorted in Item Use Command Template " + n.type.name());
				case LINKWORD:
					sb.append("/L:" + n.data.trim().toUpperCase());
					break;
				default:
					throw new RuntimeException("Unknown node type in Command Template " + n.type.name());
				}
			}
			CommandTemplate t = new CommandTemplate(sb.toString(), args);
			return t;
		}
	}


}