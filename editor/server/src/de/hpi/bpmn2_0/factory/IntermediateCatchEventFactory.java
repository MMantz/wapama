/**
 * Copyright (c) 2009
 * Philipp Giese, Sven Wagner-Boysen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.hpi.bpmn2_0.factory;

import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.diagram.EventShape;
import de.hpi.bpmn2_0.model.event.BoundaryEvent;
import de.hpi.bpmn2_0.model.event.CompensateEventDefinition;
import de.hpi.bpmn2_0.model.event.Event;
import de.hpi.bpmn2_0.model.event.IntermediateCatchEvent;
import de.hpi.bpmn2_0.model.event.MessageEventDefinition;
import de.hpi.bpmn2_0.model.event.TimerEventDefinition;

/**
 * Factory to create intermediate catching Events
 * 
 * @author Philipp Giese
 * @author Sven Wagner-Boysen
 *
 */
@StencilId({
	"IntermediateMessageEventCatching",
	"IntermediateTimerEvent",
	"IntermediateEscalationEvent",
	"IntermediateConditionalEvent",
	"IntermediateLinkEventCatching",
	"IntermediateErrorEvent",
	"IntermediateCancelEvent",
	"IntermediateCompensationEventCatching",
	"IntermediateSignalEventCatching",
	"IntermediateMultipleEventCatching",
	"IntermediateParallelMultipleEventCatching"
})

public class IntermediateCatchEventFactory extends AbstractBpmnFactory {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createBpmnElement(org.oryxeditor.server.diagram.Shape, de.hpi.bpmn2_0.factory.BPMNElement)
	 */
	@Override
	public BPMNElement createBpmnElement(Shape shape, BPMNElement parent)
			throws BpmnConverterException {
		EventShape icEventShape = (EventShape) this.createDiagramElement(shape);
		IntermediateCatchEvent icEvent = (IntermediateCatchEvent) this.createProcessElement(shape);
		icEventShape.setEventRef(icEvent);
		
		return new BPMNElement(icEventShape, icEvent, shape.getResourceId());
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createDiagramElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected Object createDiagramElement(Shape shape) {
		EventShape intermediateEventShape = new EventShape();
		this.setVisualAttributes(intermediateEventShape, shape);
		
		return intermediateEventShape;
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected BaseElement createProcessElement(Shape shape)
			throws BpmnConverterException {
		try {
			IntermediateCatchEvent icEvent = (IntermediateCatchEvent) this.invokeCreatorMethod(shape);
			icEvent.setId(shape.getResourceId());
			icEvent.setName(shape.getProperty("name"));
			
			return icEvent;
		} catch (Exception e) {
			/* Wrap exceptions into specific BPMNConverterException */
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}
		
	}
	
	@StencilId("IntermediateCompensationEventCatching")
	protected IntermediateCatchEvent createCompensateEvent(Shape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();
		CompensateEventDefinition compEvDef = new CompensateEventDefinition();
		icEvent.getEventDefinition().add(compEvDef);
		return icEvent;
	}
	
	@StencilId("IntermediateTimerEvent")
	protected IntermediateCatchEvent createTimerEvent(Shape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();
		
		icEvent.setId(shape.getResourceId());
		icEvent.setName(shape.getProperty("name"));
		
		TimerEventDefinition timerEvDef = new TimerEventDefinition();
		
		icEvent.getEventDefinition().add(timerEvDef);
		
		return icEvent;
	}
	
	@StencilId("IntermediateMessageEventCatching")
	protected IntermediateCatchEvent createMessageEvent(Shape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();
		
		icEvent.setId(shape.getResourceId());
		icEvent.setName(shape.getProperty("name"));
		
		MessageEventDefinition messageEvDef = new MessageEventDefinition();
		
		icEvent.getEventDefinition().add(messageEvDef);
		
		return icEvent;
	}
	
	public static void changeToBoundaryEvent(BPMNElement activity, BPMNElement event) {
		if(!(activity.getNode() instanceof Activity) || !(event.getNode() instanceof IntermediateCatchEvent)) {
			return;
		}
		
		BoundaryEvent bEvent = new BoundaryEvent();
		bEvent.getEventDefinition().addAll(((Event) event.getNode()).getEventDefinition());
		bEvent.setAttachedToRef((Activity) activity.getNode());
//		bEvent.setProcessRef(event.get);
		bEvent.setId(event.getNode().getId());
		bEvent.setName(((IntermediateCatchEvent) event.getNode()).getName());
		bEvent.setParallelMultiple(((IntermediateCatchEvent) event.getNode()).isParallelMultiple());
		// TODO: bEvent.setCancelActivity()
		
//		/* Refresh references */
//		int index = process.getFlowElement().indexOf(event.getNode());
//		if(index != -1) {
//			process.getFlowElement().remove(index);
//			process.getFlowElement().add(index, bEvent);
//		}
		event.setNode(bEvent);
		((EventShape) event.getShape()).setEventRef(bEvent);
		((Activity)activity.getNode()).getBoundaryEventRefs().add(bEvent);
	}
}