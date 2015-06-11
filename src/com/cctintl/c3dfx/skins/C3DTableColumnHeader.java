package com.cctintl.c3dfx.skins;

import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TableColumnBase;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import com.sun.javafx.scene.control.skin.TableColumnHeader;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;

/**
 * @author sshahine
 *
 */

public class C3DTableColumnHeader extends TableColumnHeader {

	private StackPane container = new StackPane();
	private StackPane arrowContainer = new StackPane();
	private Region arrow;
	private HBox dotsContainer;
	private Timeline arrowAnimation;
	private double currentArrowRotation = -1;
	private GridPane arrowPane;
	private boolean invalid = true;
	
	public C3DTableColumnHeader(TableViewSkinBase skin, TableColumnBase tc) {
		super(skin, tc);		
	}

	@Override protected void layoutChildren() {
		super.layoutChildren();

		double w = snapSize(getWidth()) - (snappedLeftInset() + snappedRightInset());
		container.resizeRelocate(snappedLeftInset(), 0, w, getHeight());
		
		
		if(!getChildren().contains(container)){
			invalid = true;
			container.getChildren().remove(arrowContainer);
			for(int i = 0 ; i < getChildren().size();){
				Node child = getChildren().get(i);
				container.getChildren().add(child);
			}
			getChildren().add(container);	
		}
		
		// add animation to sorting arrow
		if(invalid){
			if(container.getChildren().size() > 1 && !container.getChildren().contains(arrowContainer)){
				// setup children
				arrowPane = (GridPane) container.getChildren().get(1);
				arrow = (Region) arrowPane.getChildren().get(0);
				
				arrowContainer.getChildren().clear();
				container.getChildren().remove(1);
				container.getChildren().add(arrowContainer);
				
				for(int i = 0 ; i < arrowPane.getChildren().size();){
					Node child = arrowPane.getChildren().get(i);
					arrowContainer.getChildren().add(child);
					if(child instanceof HBox){
						dotsContainer = (HBox) child;
						dotsContainer.setMaxHeight(5);
						dotsContainer.translateYProperty().bind(Bindings.createDoubleBinding(()->{
							return arrow.getHeight() + 2;
						}, arrow.heightProperty()));
					}
				}

				arrowContainer.maxWidthProperty().bind(arrow.widthProperty());
				StackPane.setAlignment(arrowContainer, Pos.CENTER_RIGHT);
				arrowContainer.setTranslateX(-10);

				if(arrowAnimation!=null && arrowAnimation.getStatus().equals(Status.RUNNING)) arrowAnimation.stop();

				if(arrow.getRotate() == 180 && arrow.getRotate() != currentArrowRotation){				
					arrowContainer.setOpacity(0);
					arrowContainer.setTranslateY(getHeight()/4);	
					arrowAnimation = new Timeline(new KeyFrame(Duration.millis(320),
							new KeyValue(arrowContainer.opacityProperty(), 1, Interpolator.EASE_BOTH),
							new KeyValue(arrowContainer.translateYProperty(), 0, Interpolator.EASE_BOTH)));
				}else if(arrow.getRotate() == 0 && arrow.getRotate() != currentArrowRotation){						
					arrow.setRotate(-180);	
					arrowAnimation = new Timeline(new KeyFrame(Duration.millis(160),						
							new KeyValue(arrow.rotateProperty(), 0, Interpolator.EASE_BOTH),
							new KeyValue(arrowContainer.opacityProperty(), 1, Interpolator.EASE_BOTH),
							new KeyValue(arrowContainer.translateYProperty(), 0, Interpolator.EASE_BOTH) ));						
				}
				arrowAnimation.setOnFinished((finish)->currentArrowRotation = arrow.getRotate());
				arrowAnimation.play();

			}

			if(arrowContainer!=null && arrowPane!=null && container.getChildren().size() == 1 && !arrowPane.isVisible()){			
				if(arrowAnimation!=null && arrowAnimation.getStatus().equals(Status.RUNNING)) arrowAnimation.stop();
				container.getChildren().add(arrowContainer);
				arrowAnimation = new Timeline(new KeyFrame(Duration.millis(320),
						new KeyValue(arrowContainer.opacityProperty(), 0, Interpolator.EASE_BOTH),
						new KeyValue(arrowContainer.translateYProperty(), getHeight()/4, Interpolator.EASE_BOTH)));
				arrowAnimation.setOnFinished((finish)->{currentArrowRotation = -1;});
				arrowAnimation.play();
			}
		}
		
	}

}
