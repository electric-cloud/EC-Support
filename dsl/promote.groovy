import groovy.transform.BaseScript
import com.electriccloud.commander.dsl.util.BasePlugin

//noinspection GroovyUnusedAssignment
@BaseScript BasePlugin baseScript

// Variables available for use in DSL code
def pluginName = args.pluginName
def upgradeAction = args.upgradeAction
def otherPluginName = args.otherPluginName

def pluginKey = getProject("/plugins/$pluginName/project").pluginKey
def pluginDir = getProperty("/projects/$pluginName/pluginDir").value

//List of procedure steps to which the plugin configuration credentials need to be attached
// ** steps with attached credentials
def stepsWithAttachedCredentials = [
		/*[
			procedureName: 'Procedure Name',
			stepName: 'step that needs the credentials to be attached'
		 ],*/
	]
// ** end steps with attached credentials

project pluginName, {
		loadPluginProperties(pluginDir, pluginName)
		loadProcedures(pluginDir, pluginKey, pluginName, stepsWithAttachedCredentials)
		//plugin configuration metadata
		property 'ec_config', {
			form = '$[' + "/projects/${pluginName}/procedures/CreateConfiguration/ec_parameterForm]"
			property 'fields', {
				property 'desc', {
					property 'label', value: 'Description'
					property 'order', value: '1'
				}
			}
		}
		property 'pac_configurations', {
			property 'cgi-bin', {
				property 'jobMonitor.cgi', description: 'Monitors a job: waits for it to complete and reports on its success or failure.'
			}
			property 'ui_forms', {
				property 'createConfigForm', description: 'Form to create new configuration'
			}
		}

	property 'ec_visibility', value: 'all'

	/* property 'version',
		value: '1.4.6',
		description: new File(pluginDir + "/dsl/properties/CHANGELOG.txt").text
	*/
}
