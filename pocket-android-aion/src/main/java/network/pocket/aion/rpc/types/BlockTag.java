package network.pocket.aion.rpc.types;

import network.pocket.aion.util.HexStringUtil;

import java.math.BigInteger;

/**
 * Block parameter that can be an integer block number, or the string
 */
public class BlockTag {

    private BigInteger blockHeight;
    private DefaultBlock defaultBlock;

    public enum DefaultBlock {
        EARLIEST("earliest"),LATEST("latest");

        private String tagString;

        DefaultBlock(String tagString) {
            this.tagString = tagString;
        }

        public String getTagString() {
            return tagString;
        }
    }

    public BlockTag(BigInteger blockHeight) {
        this.blockHeight = blockHeight;
    }

    public BlockTag(DefaultBlock defaultBlock) {
        this.defaultBlock = defaultBlock;
    }

    public String getBlockTagString() {
        if(this.blockHeight != null) {
            return HexStringUtil.prependZeroX(this.blockHeight.toString(16));
        } else if(this.defaultBlock != null) {
            return this.defaultBlock.getTagString();
        }
        return null;
    }

    public static BlockTag tagOrLatest(BlockTag blockTag) {
        return blockTag == null ? new BlockTag(DefaultBlock.LATEST) : blockTag;
    }
}
