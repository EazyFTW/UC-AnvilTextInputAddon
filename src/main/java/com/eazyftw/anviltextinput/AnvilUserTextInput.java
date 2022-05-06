package com.eazyftw.anviltextinput;

import me.TechsCode.UltraCustomizer.UltraCustomizer;
import me.TechsCode.UltraCustomizer.base.item.XMaterial;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.*;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.datatypes.DataType;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;

public class AnvilUserTextInput extends Element {

    private final UltraCustomizer plugin;

    public AnvilUserTextInput(UltraCustomizer plugin) {
        super(plugin);

        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "Anvil User Text Input";
    }

    @Override
    public String getInternalName() {
        return "anvil-user-input-text";
    }

    @Override
    public boolean isHidingIfNotCompatible() {
        return false;
    }

    @Override
    public XMaterial getMaterial() {
        return XMaterial.WRITABLE_BOOK;
    }

    @Override
    public String[] getDescription() {
        return new String[] {
            "Will prompt the player",
            "to input text into an anvil.",
        };
    }

    @Override
    public Argument[] getArguments(ElementInfo elementInfo) {
        return new Argument[] {
                new Argument("player", "Player", DataType.PLAYER, elementInfo),
                new Argument("text", "Text", DataType.STRING, elementInfo)
        };
    }


    @Override
    public OutcomingVariable[] getOutcomingVariables(ElementInfo elementInfo) {
        return new OutcomingVariable[] {
                new OutcomingVariable("input", "Input", DataType.STRING, elementInfo)
        };
    }

    @Override
    public Child[] getConnectors(ElementInfo elementInfo) {
        return new Child[] {
                new Child(elementInfo, "text") {
                    @Override
                    public String getName() {
                        return "Text";
                    }

                    @Override
                    public String[] getDescription() {
                        return new String[] { "Text was entered." };
                    }

                    @Override
                    public XMaterial getIcon() {
                        return XMaterial.PAPER;
                    }
                },
                new Child(elementInfo, "closed") {
                    @Override
                    public String getName() {
                        return "Closed";
                    }

                    @Override
                    public String[] getDescription() {
                        return new String[] { "Anvil was closed, input was cancelled." };
                    }

                    @Override
                    public XMaterial getIcon() {
                        return XMaterial.BARRIER;
                    }
                }
        };
    }

    @Override
    public void run(ElementInfo elementInfo, ScriptInstance instance) {
        Player player = (Player) getArguments(elementInfo)[0].getValue(instance);
        String text = (String) getArguments(elementInfo)[1].getValue(instance);

        if (player != null && player.isOnline()) {
            new AnvilGUI.Builder()
                    .text(text)
                    .plugin(plugin.getBootstrap())
                    .onComplete((player1, t) -> {
                        getOutcomingVariables(elementInfo)[0].register(instance, new DataRequester() {
                            @Override
                            public Object request() {
                                return t;
                            }
                        });

                        getConnectors(elementInfo)[0].run(instance);

                        return AnvilGUI.Response.close();
                    })
                    .onClose(close -> {
                        getConnectors(elementInfo)[1].run(instance);
                    })
                    .open(player);
        }
    }
}