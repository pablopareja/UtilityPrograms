/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.util.fastautil;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class SequenceTract {
    
    public String sequence;
    public int relativeStartPosition;

    public SequenceTract(String sequence, int relativeStartPosition) {
        this.sequence = sequence;
        this.relativeStartPosition = relativeStartPosition;
    }

    public void setRelativeStartPosition(int relativeStartPosition) {
        this.relativeStartPosition = relativeStartPosition;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public int getRelativeStartPosition() {
        return relativeStartPosition;
    }

    public String getSequence() {
        return sequence;
    }
}
