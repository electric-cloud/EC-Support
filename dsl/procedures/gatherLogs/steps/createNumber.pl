#############################################################################
#
# Figure out the name of the directory in which to gather the logs.
# Either use the Zendesk ticket if if exist or create one based on date
#
# Author: L.Rochette
#
# Copyright 2015-2018 Electric Cloud, Inc.
#
#     Licensed under the Apache License, Version 2.0 (the "License");
#     you may not use this file except in compliance with the License.
#     You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
#     Unless required by applicable law or agreed to in writing, software
#     distributed under the License is distributed on an "AS IS" BASIS,
#     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#     See the License for the specific language governing permissions and
#     limitations under the License.
#
# History
# ---------------------------------------------------------------------------
# 2018-Aug-15 lrochette Modify to allow call from createTicket
#
#############################################################################

$[/plugins[EC-Admin]project/scripts/perlHeaderJSON]

my $ticket=getP('/myJob/zendesk/ticketId');
if ($ticket) {
  $ec->setProperty("/myJob/packageNumber", $ticket);
} else {
  $ec->setProperty("/myJob/packageNumber", "$[/timestamp YYYY.MM.dd]");
}

$[/plugins[EC-Admin]project/scripts/perlLibJSON]
