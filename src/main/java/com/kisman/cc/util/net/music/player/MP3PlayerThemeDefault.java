package com.kisman.cc.util.net.music.player;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The default theme for the mp3 player.
 * 
 * @version 0.1.0, June 16, 2011
 * @author Cristian Sulea ( http://cristiansulea.entrust.ro )
 */
final class MP3PlayerThemeDefault implements MP3PlayerTheme {

  public void apply(final MP3Player player) {

    final JButton playButton = new JButton();
    playButton.setText(">");
    playButton.setToolTipText("Play");
    playButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        player.play();
      }
    });

    final JButton pauseButton = new JButton();
    pauseButton.setText("||");
    pauseButton.setToolTipText("Pause");
    pauseButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        player.pause();
      }
    });

    final JButton stopButton = new JButton();
    stopButton.setText("#");
    stopButton.setToolTipText("Stop");
    stopButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        player.stop();
      }
    });

    final JButton skipBackwardButton = new JButton();
    skipBackwardButton.setText("|<");
    skipBackwardButton.setToolTipText("Skip Backward");
    skipBackwardButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        player.skipBackward();
      }
    });

    final JButton skipForwardButton = new JButton();
    skipForwardButton.setText(">|");
    skipForwardButton.setToolTipText("Skip Forward");
    skipForwardButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        player.skipForward();
      }
    });

    final JSlider volumeSlider = new JSlider();
    volumeSlider.setToolTipText("Volume");
    volumeSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        player.setVolume(volumeSlider.getValue());
      }
    });
    volumeSlider.setMinimum(0);
    volumeSlider.setMaximum(100);
    volumeSlider.setMajorTickSpacing(50);
    volumeSlider.setMinorTickSpacing(10);
    volumeSlider.setPaintTicks(true);
    volumeSlider.setPaintTrack(true);

    final JCheckBox repeatCheckBox = new JCheckBox();
    repeatCheckBox.setText("Repeat");
    repeatCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        player.setRepeat(repeatCheckBox.isSelected());
      }
    });

    final JCheckBox shuffleCheckBox = new JCheckBox();
    shuffleCheckBox.setText("Shuffle");
    shuffleCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        player.setShuffle(shuffleCheckBox.isSelected());
      }
    });

    
  }
}