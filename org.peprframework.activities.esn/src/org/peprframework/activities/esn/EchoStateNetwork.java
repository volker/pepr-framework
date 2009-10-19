/**
 * Copyright 2009 pepr Framework
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.peprframework.activities.esn;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Activity;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@XmlRootElement(name = "EchoStateNetwork")
public class EchoStateNetwork extends Activity<EchoStateNetworkInput, EchoStateNetworkOutput, EchoStateNetworkConfiguration> {

	private static final Pattern sizePattern = Pattern.compile("\\((\\d+),(\\d+)\\)");
	private static final Pattern cellPattern = Pattern.compile("\\((\\d+),(\\d+)\\) (.+)");

	private transient Algebra algebra;
	private transient DoubleMatrix2D inputWeights;
	private transient DoubleMatrix2D x;
	private transient DoubleMatrix2D internalWeights;
	private transient DoubleMatrix2D outputWeights;
	private transient DoubleMatrix2D directOutputWeights;

	@Override
	public void initialize() {
		super.initialize();

		algebra = Algebra.DEFAULT;

		// load weight matrices
		try {
			inputWeights = loadMatrix(getConfiguration().getInputWeightsFile());
			internalWeights = loadMatrix(getConfiguration().getInternalWeightsFile());
			outputWeights = loadMatrix(getConfiguration().getOutputWeightsFile());
			directOutputWeights = loadMatrix(getConfiguration().getDirectOutputWeightsFile());
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		// initialize network
		DoubleFactory2D factory = DoubleFactory2D.dense;
		x = factory.make(internalWeights.columns(), 1);
	}

	protected DoubleMatrix2D loadMatrix(String name) throws Exception {
		DoubleMatrix2D matrix = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(name)));

		try {
			// read size information and create empty matrix
			String line = reader.readLine();
			if (line != null) {
				Matcher matcher = sizePattern.matcher(line);
				if (matcher.matches()) {
					int maxY = Integer.parseInt(matcher.group(1));
					int maxX = Integer.parseInt(matcher.group(2));
					matrix = new DenseDoubleMatrix2D(maxY, maxX);
				}
			}

			// fill matrix
			while ((line = reader.readLine()) != null) {
				Matcher matcher = cellPattern.matcher(line);
				if (!matcher.matches())
					continue;

				int y = Integer.parseInt(matcher.group(1));
				int x = Integer.parseInt(matcher.group(2));
				double value = Double.parseDouble(matcher.group(3));

				matrix.set(y - 1, x - 1, value);
			}

			return matrix;
		} finally {
			reader.close();
		}
	}

	@Override
	public EchoStateNetworkOutput handleMessage(EchoStateNetworkInput msg) {
		EchoStateNetworkOutput output = new EchoStateNetworkOutput();
		output.prediction = new double[msg.features.length];
		
		for (int f = 0; f < msg.features.length; f++) {
			DenseDoubleMatrix2D input = new DenseDoubleMatrix2D(msg.features[f].length, 1);
			for (int i = 0; i < msg.features[f].length; i++) {
				input.set(i, 0, msg.features[f][i]);
			}

			DoubleMatrix2D tmp1 = algebra.mult(inputWeights, input);
			DoubleMatrix2D tmp2 = algebra.mult(internalWeights, x);

			for (int i = 0; i < internalWeights.rows(); i++) {
				double element = Math.tanh(tmp1.get(i, 0) + tmp2.get(i, 0));
				x.set(i, 0, element);
			}

			double y = Math.tanh(outputWeights.viewColumn(0).zDotProduct(x.viewColumn(0)) + directOutputWeights.viewColumn(0).zDotProduct(input.viewColumn(0)));
			y /= 0.5d;
//			prediction = y * (1d - getConfiguration().getAlpha()) + prediction * getConfiguration().getAlpha();

			output.prediction[f] = y;
		}
		return output;
	}

	@Override
	public void terminate() {
		super.terminate();
	}

	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#getId()
	 */
	@Override
	public String getId() {
		return "org.peprframework.activities.esn";
	}
}
